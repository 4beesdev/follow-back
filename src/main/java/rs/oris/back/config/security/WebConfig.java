package rs.oris.back.config.security;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Application-wide HTTP client configuration.
 *
 * <p>Provides a shared {@link RestTemplate} bean backed by a pooled Apache HTTP
 * client so that outbound HTTP calls reuse connections instead of opening a new
 * socket for every request.</p>
 */
@Configuration
public class WebConfig {

    /** Connect timeout for outbound HTTP (e.g. Teltonika/GS100/Galeb APIs). */
    public static final int HTTP_CONNECT_TIMEOUT_MS = 30_000;

    /** Read timeout for outbound HTTP — 20 minutes (slow report downstream calls). */
    public static final int HTTP_READ_TIMEOUT_MS = 20 * 60 * 1000;

    /**
     * Creates the application's main {@link RestTemplate} with a connection-pooled
     * Apache HTTP client.
     *
     * <ul>
     *   <li><b>Pool size:</b> up to 50 total connections, 20 per target host —
     *       prevents connection exhaustion under load.</li>
     *   <li><b>Timeouts:</b> shared prod-tuned constants (30s connect, 20min read).</li>
     * </ul>
     */
    @Bean
    public RestTemplate restTemplate() {
        // Connection pool — reuses TCP connections across requests
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(50);              // max connections in the pool
        cm.setDefaultMaxPerRoute(20);    // max connections per target host

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setConnectTimeout(HTTP_CONNECT_TIMEOUT_MS);
        factory.setReadTimeout(HTTP_READ_TIMEOUT_MS);

        return new RestTemplate(factory);
    }

    public static RestTemplate createRestTemplate() {
        return createRestTemplate(HTTP_CONNECT_TIMEOUT_MS, HTTP_READ_TIMEOUT_MS);
    }

    /**
     * Creates a standalone {@link RestTemplate} with explicit connect/read timeouts
     * (prod fix 0fb6c61 — used at call sites that build their own RestTemplate).
     */
    public static RestTemplate createRestTemplate(int connectTimeout, int readTimeout) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        return new RestTemplate(factory);
    }
}
