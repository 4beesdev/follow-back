package rs.oris.back.config.security;

public class SecurityConstants {
    /**
     * konstante koje se koriste na vise razlicitih mesta
     * ova klasa je uvedena da bi bilo st omanje hard-coded inputa
     */
    public static final String SECRET = "SecretKeyToGenJWTs";
    public static final long EXPIRATION_TIME = 864_000_000; // 10 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/api/user/sign-up";

}