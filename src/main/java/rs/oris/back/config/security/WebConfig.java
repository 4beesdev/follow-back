package rs.oris.back.config.security;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
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

    /**
     * Creates the application's main {@link RestTemplate} with a connection-pooled
     * Apache HTTP client.
     *
     * <ul>
     *   <li><b>Pool size:</b> up to 50 total connections, 20 per target host —
     *       prevents connection exhaustion under load.</li>
     *   <li><b>Connect timeout:</b> 5 seconds — fail fast if the target is unreachable.</li>
     *   <li><b>Read timeout:</b> 30 seconds — generous, as some downstream calls
     *       (e.g. GPS data) may be slow.</li>
     * </ul>
     *
     * <p>Note: The health-check endpoint uses its own RestTemplate with shorter
     * timeouts (see {@link rs.oris.back.controller.AdminHealthController}).</p>
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
        factory.setConnectTimeout(5000);   // 5s connect timeout
        factory.setReadTimeout(30000);     // 30s read timeout

        return new RestTemplate(factory);
    }
}
