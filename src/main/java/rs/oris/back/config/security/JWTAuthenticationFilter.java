package rs.oris.back.config.security;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import rs.oris.back.controller.wrapper.ForbiddenException2;
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
 * Ova klasa menja springov Authentication filter
 * Autentikacija je provera korisnika
 *
 */
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private UserRepository userRepository = SpringContext.getBean(UserRepository.class);
    private AuthenticationManager authenticationManager;
    private User credentials;
    /**
     * Definisan custom Gson converter sa implementiranom logikom za koja polja ne treba da se beleze u json fajl
     *
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
    /**
     * Pristupa se iz web security filterchain-a
     * @param authenticationManager
     */

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * Proverava da li se poklapaju username i password koje cita iz requesta pozivajuci authentication manager
     *
     * @param req
     * @param res
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        try {
            credentials = new ObjectMapper().readValue(req.getInputStream(), User.class);
            User u = userRepository.findByUsername(credentials.getUsername());
            if (u == null)
                throw new ForbiddenException2("User does not exist");
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword(), new ArrayList<>()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Generise JWT u slucaju uspesne autentikacije, u njemu pamti username, kad istice (10 dana)
     *
     * @param req
     * @param res
     * @param chain
     * @param auth
     * @throws IOException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) throws IOException {
        String token = JWT.create()
                .withSubject(((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .sign(HMAC512(SecurityConstants.SECRET.getBytes()));
        User user = userRepository.findByUsername(credentials.getUsername());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token", SecurityConstants.TOKEN_PREFIX + token);
        PrintWriter out = res.getWriter();
        out.print(gson.toJson(jsonObject));
        res.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
    }

    /**
     * U slucaju neuspesne autentikacije samo prosledi nadklasi, tj prakticno kao da nije overrajdovana metoda
     *
     * @param request
     * @param response
     * @param failed
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);
    }
}
