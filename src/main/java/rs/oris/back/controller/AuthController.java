package rs.oris.back.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.config.security.SecurityConstants;
import rs.oris.back.domain.User;
import rs.oris.back.service.UserService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Authentication controller responsible for token refresh.
 *
 * <p>The login endpoint ({@code POST /login}) is handled by
 * {@link rs.oris.back.config.security.JWTAuthenticationFilter}.
 * This controller only exposes {@code POST /api/auth/refresh} which allows
 * clients to exchange a valid refresh token for a new access token.</p>
 *
 * <p><b>Refresh flow:</b></p>
 * <ol>
 *   <li>Client sends the refresh token in the request body.</li>
 *   <li>The token is verified (signature, expiration, and type claim).</li>
 *   <li>The user is re-validated (must still exist and be active).</li>
 *   <li>A fresh access token is issued.</li>
 *   <li>If the refresh token is past its half-life, a new refresh token
 *       is also issued (rolling refresh) to extend the session seamlessly.</li>
 * </ol>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * Exchanges a valid refresh token for a new access token.
     *
     * <p><b>Validation steps:</b></p>
     * <ol>
     *   <li>Verify the refresh token's HMAC signature and expiration.</li>
     *   <li>Ensure the token's {@code type} claim is "refresh" (reject access tokens).</li>
     *   <li>Re-check the user in the database — accounts deleted or deactivated
     *       after token issuance are rejected here.</li>
     * </ol>
     *
     * <p><b>Rolling refresh:</b> If the refresh token has passed its half-life
     * (i.e. more than 3.5 days old out of 7), a brand-new refresh token is returned
     * alongside the access token. This lets active users stay logged in
     * indefinitely while inactive sessions expire naturally.</p>
     *
     * @param body JSON body containing a {@code refreshToken} field
     * @return new access token (and optionally a new refresh token)
     */
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errorResponse("Refresh token is required"));
        }

        try {
            // Step 1: Verify the refresh token signature and expiration
            DecodedJWT decoded = JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()))
                    .build()
                    .verify(refreshToken);

            String username = decoded.getSubject();
            String tokenType = decoded.getClaim("type").asString();

            // Step 2: Reject access tokens used in place of a refresh token
            if (!"refresh".equals(tokenType)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(errorResponse("Invalid token type"));
            }

            // Step 3: Re-validate that the user still exists and is active in the DB
            User user;
            try {
                user = userService.findByUsername(username);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(errorResponse("User not found"));
            }

            if (user == null || !Boolean.TRUE.equals(user.getActive())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(errorResponse("User is inactive"));
            }

            // Step 4: Issue a fresh short-lived access token (30 min)
            String newAccessToken = JWT.create()
                    .withSubject(username)
                    .withClaim("type", "access")
                    .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                    .sign(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()));

            Map<String, String> response = new HashMap<>();
            response.put("token", SecurityConstants.TOKEN_PREFIX + newAccessToken);

            // Step 5: Rolling refresh — if the refresh token is past its half-life,
            // issue a replacement so the user doesn't have to log in again.
            long refreshExpMs = decoded.getExpiresAt().getTime();
            long refreshIssuedMs = refreshExpMs - SecurityConstants.REFRESH_EXPIRATION_TIME;
            long halfLife = SecurityConstants.REFRESH_EXPIRATION_TIME / 2;
            if (System.currentTimeMillis() > refreshIssuedMs + halfLife) {
                String newRefreshToken = JWT.create()
                        .withSubject(username)
                        .withClaim("type", "refresh")
                        .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.REFRESH_EXPIRATION_TIME))
                        .sign(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()));
                response.put("refreshToken", newRefreshToken);
            }

            return ResponseEntity.ok(response);

        } catch (JWTVerificationException e) {
            // Refresh token is expired or has been tampered with
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse("Invalid or expired refresh token"));
        }
    }

    /** Builds a simple JSON-serializable error map for 4xx responses. */
    private Map<String, String> errorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}
