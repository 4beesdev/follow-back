package rs.oris.back.controller;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import rs.oris.back.config.MongoServerConfig;

import rs.oris.back.domain.Vehicle;
import rs.oris.back.repository.VehicleRepository;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.net.Socket;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Admin health-check endpoint that probes all dependent services in parallel.
 *
 * <p>Uses a dedicated {@link RestTemplate} and a fixed-size thread pool so that
 * health checks never interfere with regular application traffic. Each individual
 * check has a short timeout (3–5 s), and all checks share an overall 6-second
 * deadline so the endpoint responds quickly even when services are down.</p>
 *
 * <p>Services checked: PostgreSQL, follow-gps-data, MongoDB (via gps-data),
 * the API itself (self-check with memory stats), and two GPS receiver TCP ports.</p>
 */
@RestController
@RequestMapping("/api/admin")
public class AdminHealthController {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private MongoServerConfig mongoServerConfig;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Value("${server.port:8000}")
    private int serverPort;

    /**
     * Dedicated RestTemplate for health checks with short timeouts (3s).
     * Separate from the app's main RestTemplate so health checks don't block.
     */
    private RestTemplate healthRestTemplate;

    /**
     * Dedicated thread pool for running health checks in parallel.
     * Fixed size of 6 threads (one per service check).
     */
    private ExecutorService healthExecutor;

    /**
     * Initializes the health-check infrastructure on startup:
     * <ul>
     *   <li>A pooled HTTP client (max 10 connections, 5 per route) with aggressive
     *       timeouts (3 s connect, 5 s read) so a single slow service cannot
     *       stall the entire health check.</li>
     *   <li>A fixed thread pool of 6 threads — one per service check — so all
     *       probes run concurrently and the endpoint returns in ~max(single check)
     *       rather than sum(all checks).</li>
     * </ul>
     */
    @PostConstruct
    public void init() {
        CloseableHttpClient httpClient = HttpClients.custom()
                .setMaxConnTotal(10)       // max total connections in the pool
                .setMaxConnPerRoute(5)     // max connections per target host
                .build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setConnectTimeout(3000);   // 3s connect timeout
        factory.setReadTimeout(5000);      // 5s read timeout (was 30s!)
        this.healthRestTemplate = new RestTemplate(factory);
        this.healthExecutor = Executors.newFixedThreadPool(6); // one thread per health check
    }

    /**
     * Runs all service health checks in parallel and returns a summary.
     * Each check has its own timeout; the overall collection phase is capped at 6 seconds.
     * Returns status "UP" if all services are healthy, "DEGRADED" otherwise.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        long overallStart = System.currentTimeMillis();
        Map<String, Object> result = new LinkedHashMap<>();

        // Run ALL health checks in parallel
        String gpsDataBaseUrl = mongoServerConfig.getMongoBaseUrl();
        List<Future<Map<String, Object>>> futures = new ArrayList<>();

        futures.add(healthExecutor.submit(this::checkPostgres));
        futures.add(healthExecutor.submit(() -> checkHttpService("follow-gps-data",
                gpsDataBaseUrl + "/api/12345")));
        futures.add(healthExecutor.submit(this::selfCheck));
        futures.add(healthExecutor.submit(() -> checkHttpService("mongodb-via-gps-data",
                gpsDataBaseUrl + "/api/admin/gps/health")));
        futures.add(healthExecutor.submit(() -> checkTcpPort("gps-gs100", "gps-gs100", 9876)));
        futures.add(healthExecutor.submit(() -> checkTcpPort("gps-teltonika", "gps-teltonika", 9877)));

        // Collect results with a 6 second overall timeout
        List<Map<String, Object>> services = new ArrayList<>();
        for (Future<Map<String, Object>> f : futures) {
            try {
                services.add(f.get(6, TimeUnit.SECONDS));
            } catch (TimeoutException e) {
                Map<String, Object> timeout = new LinkedHashMap<>();
                timeout.put("name", "unknown");
                timeout.put("status", "DOWN");
                timeout.put("error", "Health check timed out");
                services.add(timeout);
            } catch (Exception e) {
                Map<String, Object> err = new LinkedHashMap<>();
                err.put("name", "unknown");
                err.put("status", "DOWN");
                err.put("error", e.getMessage());
                services.add(err);
            }
        }

        result.put("timestamp", System.currentTimeMillis());
        result.put("totalCheckTime", System.currentTimeMillis() - overallStart);
        result.put("services", services);

        boolean allHealthy = services.stream()
                .allMatch(s -> "UP".equals(s.get("status")));
        result.put("status", allHealthy ? "UP" : "DEGRADED");

        return ResponseEntity.ok(result);
    }

    /** Checks PostgreSQL connectivity by executing a simple {@code SELECT 1} query. */
    private Map<String, Object> checkPostgres() {
        Map<String, Object> svc = new LinkedHashMap<>();
        svc.put("name", "postgresql");
        svc.put("type", "database");
        long start = System.currentTimeMillis();
        try (Connection conn = dataSource.getConnection()) {
            conn.createStatement().execute("SELECT 1");
            svc.put("status", "UP");
        } catch (Exception e) {
            svc.put("status", "DOWN");
            svc.put("error", e.getMessage());
        }
        svc.put("responseTime", System.currentTimeMillis() - start);
        return svc;
    }

    /** Quick self-check: always UP, reports current JVM memory usage and port. */
    private Map<String, Object> selfCheck() {
        Map<String, Object> svc = new LinkedHashMap<>();
        svc.put("name", "follow-api");
        svc.put("type", "service");
        svc.put("status", "UP");
        svc.put("responseTime", 0);
        svc.put("port", serverPort);

        Runtime rt = Runtime.getRuntime();
        svc.put("memoryUsedMB", (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024));
        svc.put("memoryMaxMB", rt.maxMemory() / (1024 * 1024));
        return svc;
    }

    /** Probes an HTTP service; treats HTTP 404 as UP (service reachable but resource missing). */
    private Map<String, Object> checkHttpService(String name, String url) {
        Map<String, Object> svc = new LinkedHashMap<>();
        svc.put("name", name);
        svc.put("type", "service");
        long start = System.currentTimeMillis();
        try {
            healthRestTemplate.getForEntity(url, String.class);
            svc.put("status", "UP");
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("404")) {
                svc.put("status", "UP");
            } else {
                svc.put("status", "DOWN");
                svc.put("error", e.getMessage() != null ?
                        e.getMessage().substring(0, Math.min(200, e.getMessage().length())) : "Unknown");
            }
        }
        svc.put("responseTime", System.currentTimeMillis() - start);
        return svc;
    }

    /** Verifies a TCP port is accepting connections (used for GPS receiver services). */
    private Map<String, Object> checkTcpPort(String name, String host, int port) {
        Map<String, Object> svc = new LinkedHashMap<>();
        svc.put("name", name);
        svc.put("type", "gps-receiver");
        svc.put("port", port);
        long start = System.currentTimeMillis();
        try (Socket socket = new Socket()) {
            socket.connect(new java.net.InetSocketAddress(host, port), 3000);
            svc.put("status", "UP");
        } catch (Exception e) {
            svc.put("status", "DOWN");
            svc.put("error", e.getMessage());
        }
        svc.put("responseTime", System.currentTimeMillis() - start);
        return svc;
    }

    /**
     * Returns a lightweight mapping of all active vehicles: IMEI → registration + firm.
     * Used by the superadmin monitoring dashboard to enrich the IMEI table with
     * company name and registration plate without fetching full Vehicle entities.
     */
    @GetMapping("/vehicle-map")
    public ResponseEntity<List<Map<String, Object>>> getVehicleMap() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        List<Map<String, Object>> result = vehicles.stream()
                .filter(v -> v.getImei() != null && !v.getImei().isEmpty() && v.getDeletedDate() == null)
                .map(v -> {
                    Map<String, Object> entry = new LinkedHashMap<>();
                    entry.put("imei", v.getImei());
                    entry.put("registration", v.getRegistration() != null ? v.getRegistration() : "");
                    entry.put("firmId", v.getFirm() != null ? v.getFirm().getFirmId() : null);
                    entry.put("firmName", v.getFirm() != null ? v.getFirm().getName() : "Nepoznata");
                    return entry;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}
