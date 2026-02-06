package rs.oris.back.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * JWT Authorization filter — intercepts every incoming HTTP request to validate
 * the access token in the {@code Authorization} header.
 *
 * <p><b>Processing flow:</b></p>
 * <ol>
 *   <li>If the request has no Authorization header (or it doesn't start with "Bearer "),
 *       the request is passed through — public endpoints are handled by permitAll().</li>
 *   <li>The JWT is verified (signature + expiration) via {@link #getAuthentication}.</li>
 *   <li>On success, the authenticated user is placed into the SecurityContext so
 *       downstream code (controllers, services) can access it via {@link AuthUtil}.</li>
 *   <li>On expired token — returns 401 with a JSON hint to refresh the token.</li>
 *   <li>On invalid/tampered token — returns 401 with a generic error message.</li>
 * </ol>
 */
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private static final Logger log = LoggerFactory.getLogger(JWTAuthorizationFilter.class);

    public JWTAuthorizationFilter(AuthenticationManager authManager) {
        super(authManager);
    }

    /**
     * Intercepts every HTTP request to check for a valid JWT.
     * Delegates to {@link #getAuthentication} for token parsing and verification.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(SecurityConstants.HEADER_STRING);

        // No token present — let the request through (public endpoints will work;
        // protected endpoints will be rejected by Spring Security's access rules).
        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        try {
            // Verify the token and set the authenticated user in the SecurityContext
            UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(req, res);
        } catch (TokenExpiredException e) {
            // Access token has expired — client should call POST /api/auth/refresh
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
            res.getWriter().write("{\"error\":\"Token expired\",\"message\":\"Access token has expired. Please refresh.\"}");
        } catch (JWTVerificationException e) {
            // Token is malformed, tampered with, or otherwise invalid
            log.warn("Invalid JWT token from {}", req.getRemoteAddr());
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
            res.getWriter().write("{\"error\":\"Invalid token\",\"message\":\"The provided token is invalid.\"}");
        }
    }

    /**
     * Strips the "Bearer " prefix, verifies the JWT signature and expiration using
     * the shared HMAC512 secret, and extracts the username (subject claim).
     *
     * @return an authentication token containing the username, or null if absent
     * @throws TokenExpiredException      if the token's exp claim is in the past
     * @throws JWTVerificationException   if the signature is invalid or claims are malformed
     */
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(SecurityConstants.HEADER_STRING);
        if (token != null) {
            // Verify signature + expiration and extract the username from the "sub" claim
            String user = JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()))
                    .build()
                    .verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
                    .getSubject();
            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
            }
        }
        return null;
    }
}
