package rs.oris.back.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static rs.oris.back.config.security.SecurityConstants.SIGN_UP_URL;
import static rs.oris.back.config.security.SecurityConstants.REFRESH_URL;

/**
 * Main Spring Security configuration.
 *
 * <p>Configures the HTTP security filter chain, CORS policy, session management,
 * and wires the custom JWT authentication/authorization filters into the pipeline.
 * The application is fully stateless — no HTTP sessions are created.</p>
 */
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    /** Custom UserDetailsService that loads users from the database. */
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    /** BCrypt password encoder used for hashing and verifying passwords. */
    @Autowired
    @Lazy
    private BCryptPasswordEncoder bCryptPasswordEncoder;



    /**
     * Configures the HTTP security filter chain.
     *
     * <ul>
     *   <li>Enables CORS (config from {@link #corsConfigurationSource()}).</li>
     *   <li>Disables CSRF — safe because the API is stateless and uses JWT tokens.</li>
     *   <li>Whitelists public endpoints (sign-up, token refresh, languages, integrations, etc.).</li>
     *   <li>All other endpoints require a valid JWT.</li>
     *   <li>Adds {@link JWTAuthenticationFilter} (handles POST /login).</li>
     *   <li>Adds {@link JWTAuthorizationFilter} (validates JWT on every request).</li>
     *   <li>Sets session policy to STATELESS — no server-side session is ever created.</li>
     * </ul>
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.cors().and().csrf().disable().authorizeRequests()
                // --- Public endpoints (no JWT required) ---
                .antMatchers(HttpMethod.POST, SIGN_UP_URL).permitAll()
                .antMatchers(HttpMethod.POST, REFRESH_URL).permitAll()
                .antMatchers(HttpMethod.GET, "/api/language/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/integration/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/firm/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/perkela","/api/notifications/all").permitAll()
                .antMatchers(HttpMethod.POST, "/api/single-notification","/api/notification-modal/update").permitAll()
                .antMatchers(HttpMethod.PATCH,"/api/vehicle/*/update-mileage").permitAll()
                // --- All remaining endpoints require authentication ---
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager()))   // handles login
                .addFilter(new JWTAuthorizationFilter(authenticationManager()))    // validates tokens
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);           // no HTTP sessions
    }

    /**
     * Wires the custom {@link UserDetailsServiceImpl} and BCrypt encoder into
     * Spring's AuthenticationManager so that login credentials are verified
     * against the database with hashed password comparison.
     */
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    /**
     * CORS configuration - restricts allowed origins.
     * Reads CORS_ALLOWED_ORIGINS env var (comma-separated), falls back to localhost for dev.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        String originsEnv = System.getenv("CORS_ALLOWED_ORIGINS");
        List<String> allowedOrigins;
        if (originsEnv != null && !originsEnv.isEmpty()) {
            allowedOrigins = Arrays.asList(originsEnv.split(","));
        } else {
            // Default for local development
            allowedOrigins = Arrays.asList(
                "http://localhost:8888",
                "http://localhost:8000",
                "http://localhost:3000",
                "https://gps.4bees.io"
            );
        }
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
        configuration.setExposedHeaders(Arrays.asList("x-auth-token"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Shared BCryptPasswordEncoder bean, injected wherever password hashing
     * or verification is needed (e.g. user registration, login).
     * Lazy-initialized to avoid circular dependency issues during startup.
     */
    @Bean
    @Lazy
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
