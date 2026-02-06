package rs.oris.back.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility class for extracting the currently authenticated username
 * from the Spring Security context.
 *
 * <p>Eliminates the need for controllers to manually parse the JWT.
 * The username is placed into the SecurityContext by
 * {@link JWTAuthorizationFilter} on every authenticated request.</p>
 *
 * <p><b>Usage in any controller or service:</b></p>
 * <pre>
 *   String username = AuthUtil.getCurrentUsername();
 *   // username is null when the request is unauthenticated (public endpoint)
 * </pre>
 */
public class AuthUtil {

    private AuthUtil() {
        // Utility class, no instantiation
    }

    /**
     * Returns the username of the currently authenticated user
     * from the Spring Security context (set by JWTAuthorizationFilter).
     *
     * @return username or null if not authenticated
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            return authentication.getPrincipal().toString();
        }
        return null;
    }
}
