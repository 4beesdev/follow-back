package rs.oris.back.config.security;

/**
 * Central security configuration holding all JWT-related constants.
 *
 * <p>Defines the JWT signing secret, token lifetimes, HTTP header names,
 * and public endpoint URLs used across the authentication/authorization filters.
 * Values are read from environment variables where possible so that production
 * deployments never rely on hard-coded defaults.</p>
 */
public class SecurityConstants {
    /**
     * JWT secret - reads from environment variable JWT_SECRET.
     * Falls back to a default ONLY for local development.
     * In production, JWT_SECRET MUST be set as an environment variable.
     */
    public static final String SECRET = System.getenv("JWT_SECRET") != null
            ? System.getenv("JWT_SECRET")
            : "CHANGE_ME_IN_PRODUCTION_" + System.getProperty("user.name", "dev");

    /**
     * Access token expiration: 30 minutes (was 10 days - reduced for security).
     * Short-lived access tokens limit the damage window if a token is compromised.
     */
    public static final long EXPIRATION_TIME = 1_800_000; // 30 minutes

    /**
     * Refresh token expiration: 7 days.
     * Used to obtain new access tokens without re-entering credentials.
     */
    public static final long REFRESH_EXPIRATION_TIME = 604_800_000; // 7 days

    /** Prefix prepended to every JWT in the Authorization header. */
    public static final String TOKEN_PREFIX = "Bearer ";

    /** HTTP header that carries the JWT. */
    public static final String HEADER_STRING = "Authorization";

    /** Public endpoint for user registration (no token required). */
    public static final String SIGN_UP_URL = "/api/user/sign-up";

    /** Public endpoint for refreshing an expired access token. */
    public static final String REFRESH_URL = "/api/auth/refresh";

}