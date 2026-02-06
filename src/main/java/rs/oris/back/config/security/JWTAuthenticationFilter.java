package rs.oris.back.config.security;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import rs.oris.back.domain.User;
import rs.oris.back.repository.UserRepository;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

/**
 * Custom Spring Security authentication filter that handles user login (POST /login).
 *
 * <p><b>Login flow:</b></p>
 * <ol>
 *   <li>{@link #attemptAuthentication} reads username/password from the request body.</li>
 *   <li>The user is looked up in the database; inactive accounts are rejected.</li>
 *   <li>Spring's AuthenticationManager verifies the credentials against the BCrypt hash.</li>
 *   <li>On success, {@link #successfulAuthentication} generates a short-lived access token
 *       (30 min) and a long-lived refresh token (7 days), returning both as JSON.</li>
 *   <li>On failure, {@link #unsuccessfulAuthentication} returns a generic 401 JSON error
 *       without leaking internal details.</li>
 * </ol>
 */
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private UserRepository userRepository = SpringContext.getBean(UserRepository.class);
    private AuthenticationManager authenticationManager;
    /** Temporarily holds the parsed credentials during authentication attempt. */
    private User credentials;

    /**
     * Custom Gson instance with an exclusion strategy that serializes only
     * the first occurrence of each field type — used to build the login response JSON.
     */
    private Gson gson = new GsonBuilder()
            .setExclusionStrategies(new ExclusionStrategy() {
                private Set<Class> usedClasses = new HashSet<>();

                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }

                /**
                 * Custom field exclusion goes here
                 */
                public boolean shouldSkipField(FieldAttributes f) {
                    if (usedClasses.contains(f.getDeclaredClass())) {
                        return false;
                    }
                    usedClasses.add(f.getDeclaredClass());
                    return true;
                }

            })
            /**
             * Use serializeNulls method if you want To serialize null values
             * By default, Gson does not serialize null values
             */
            .serializeNulls()
            .create();
    /** Called from {@link WebSecurity} when building the filter chain. */
    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * Reads username and password from the JSON request body, validates that
     * the user exists and is active, then delegates credential verification
     * to Spring's AuthenticationManager (BCrypt comparison).
     *
     * <p>Error handling strategy: AuthenticationExceptions are re-thrown so
     * Spring Security can invoke {@link #unsuccessfulAuthentication}.
     * IOExceptions (malformed body) and unexpected errors are wrapped in
     * AuthenticationServiceException — internal details are never exposed.</p>
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        try {
            // Step 1: Parse credentials from the request body
            credentials = new ObjectMapper().readValue(req.getInputStream(), User.class);

            // Step 2: Check the user exists in the database
            User u = userRepository.findByUsername(credentials.getUsername());
            if (u == null) {
                throw new org.springframework.security.authentication.BadCredentialsException("Invalid credentials");
            }

            // Step 3: Reject inactive/disabled accounts
            if (!Boolean.TRUE.equals(u.getActive())) {
                throw new org.springframework.security.authentication.DisabledException("User account is disabled");
            }

            // Step 4: Delegate to AuthenticationManager for BCrypt password check
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword(), new ArrayList<>())
            );
        } catch (AuthenticationException e) {
            throw e; // Let Spring Security handle authentication exceptions
        } catch (IOException e) {
            throw new org.springframework.security.authentication.AuthenticationServiceException("Invalid request format");
        } catch (Exception e) {
            // Log internally but don't expose details to client
            logger.error("Authentication error for user: " + (credentials != null ? credentials.getUsername() : "unknown"));
            throw new org.springframework.security.authentication.AuthenticationServiceException("Authentication failed");
        }
    }

    /**
     * Called after successful credential verification.
     * Generates two JWT tokens and writes them as JSON to the response body:
     * <ul>
     *   <li><b>Access token</b> (30 min) — used in the Authorization header for API calls.</li>
     *   <li><b>Refresh token</b> (7 days) — used to obtain a new access token
     *       via {@code POST /api/auth/refresh} without re-entering credentials.</li>
     * </ul>
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) throws IOException {
        String username = ((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername();

        // Short-lived access token (30 minutes)
        String accessToken = JWT.create()
                .withSubject(username)
                .withClaim("type", "access")
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .sign(HMAC512(SecurityConstants.SECRET.getBytes()));

        // Long-lived refresh token (7 days)
        String refreshToken = JWT.create()
                .withSubject(username)
                .withClaim("type", "refresh")
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.REFRESH_EXPIRATION_TIME))
                .sign(HMAC512(SecurityConstants.SECRET.getBytes()));

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token", SecurityConstants.TOKEN_PREFIX + accessToken);
        jsonObject.addProperty("refreshToken", refreshToken);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();
        out.print(gson.toJson(jsonObject));
        res.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + accessToken);
    }

    /**
     * Returns a clean JSON error response on authentication failure.
     * Does not expose internal error details or stack traces to the client.
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JsonObject error = new JsonObject();
        error.addProperty("error", "Authentication failed");
        error.addProperty("message", "Invalid username or password");
        response.getWriter().print(error.toString());
    }
}
