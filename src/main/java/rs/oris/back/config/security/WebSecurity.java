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

import java.util.Arrays;

import static rs.oris.back.config.security.SecurityConstants.SIGN_UP_URL;

@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    /**
     * Reimplementirana klasa koja nasledjuje UserDetailsService sa ciljem
     * da izvadi korisnike iz baze
     */
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    /**
     * Definisam encoder za lozinke, znaci sve sifre u bazi ce imati BCRYPT enkripciju
     */
    @Autowired
    @Lazy
    private BCryptPasswordEncoder bCryptPasswordEncoder;



    /**
     * Definisana zastita za http zahteve, dozvoljen je pristup za konkretne navedene rute i neregistrovanim korisnicima
     * kao i za sign-up stranicu, a za ostale je neophodno da postoji okrisnik u sistemu
     * Iskljucen je csrf i postavljena stateless sesija i koriste se jos dva custom filtera za autentikaciju i autorizaciju
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.cors().and().csrf().disable().authorizeRequests().antMatchers(HttpMethod.POST, SIGN_UP_URL).permitAll()
                .antMatchers(HttpMethod.GET, "/api/language/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/integration/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/firm/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/perkela","/api/notifications/all").permitAll()
                .antMatchers(HttpMethod.POST, "/api/single-notification","/api/notification-modal/update").permitAll()
                .antMatchers(HttpMethod.PATCH,"/api/vehicle/*/update-mileage").permitAll()
//                .and().authorizeRequests().anyRequest().permitAll()
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager()))
                .addFilter(new JWTAuthorizationFilter(authenticationManager()))
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    /**
     * Auth manageru se prosledjuju userDetailsService i encoder koji su definisani u Bean-ovima dole
     * @param auth
     * @throws Exception
     */
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    /**
     * Definisana cors zastitu, kao i moguce domene (svi) i http metode, dozvoljeni headeri
     * @return
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
        configuration.setExposedHeaders(Arrays.asList("x-auth-token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Bean za sve autowired password encodere koji se koriste u drugim klasama
     * @return
     */
    @Bean
    @Lazy
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
