package com.brainspark.nursepulse.platform.iam.infrastructure.authorization.sfs.configuration;

import com.brainspark.nursepulse.platform.iam.infrastructure.authorization.sfs.pipeline.BearerAuthorizationRequestFilter;
import com.brainspark.nursepulse.platform.iam.infrastructure.hashing.bcrypt.BCryptHashingService;
import com.brainspark.nursepulse.platform.iam.infrastructure.tokens.jwt.BearerTokenService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

/**
 * Web Security Configuration.
 * <p>
 * This class is responsible for configuring the web security.
 * It enables the method security and configures the security filter chain.
 * It includes the authentication manager, the authentication provider, the password encoder and the authentication entry point.
 * </p>
 */
@Configuration
@EnableMethodSecurity
public class WebSecurityConfiguration {

    private final UserDetailsService userDetailsService;

    private final BearerTokenService tokenService;

    private final BCryptHashingService hashingService;

    private final AuthenticationEntryPoint unauthorizedRequestHandler;

    private final AccessDeniedHandler forbiddenRequestHandler;

    /**
     * This method creates the Bearer Authorization Request Filter.
     * @return The Bearer Authorization Request Filter
     * @see BearerAuthorizationRequestFilter
     */
    @Bean
    public BearerAuthorizationRequestFilter authorizationRequestFilter() {
        return new BearerAuthorizationRequestFilter(tokenService, userDetailsService);
    }

    /**
     * This method creates the authentication manager.
     * @param authenticationConfiguration The {@link AuthenticationConfiguration} object with the authentication configuration
     * @return The {@link AuthenticationManager} instance from the authentication configuration
     *
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * This method creates the authentication provider.
     * @return The {@link DaoAuthenticationProvider} authentication provider with the user details service and the password encoder
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider.setPasswordEncoder(hashingService);
        return authenticationProvider;
    }

    /**
     * This method creates the password encoder.
     * @return The {@link PasswordEncoder} instance with the hashing service
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return hashingService;
    }

    /**
     * This method creates the security filter chain.
     * It also configures the http security.
     *
     * @param http The {@link HttpSecurity} object to configure with the security filter chain
     * @return The {@link SecurityFilterChain} instance with the application http security configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(configurer -> configurer.configurationSource(_ -> {
            var cors = new CorsConfiguration();
            cors.setAllowedOriginPatterns(List.of(
                    "http://localhost:4200",
                    "https://care-labs-nursepulse.netlify.app",
                    "https://*.netlify.app",
                    "https://front-nursepulse.vercel.app",
                    "https://*.vercel.app",
                    "https://backpulsereport-production-7576.up.railway.app"
            ));
            cors.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
            cors.setAllowedHeaders(List.of("*"));
            cors.setExposedHeaders(List.of("Location"));
            cors.setAllowCredentials(true);
            return cors;
        }));
        http.csrf(csrfConfigurer -> csrfConfigurer.disable())
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(unauthorizedRequestHandler)
                        .accessDeniedHandler(forbiddenRequestHandler))
                .sessionManagement( customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/api/v1/authentication/**",
                                "/error",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/webjars/**").permitAll()

                        // IAM administration
                        .requestMatchers("/api/v1/users/**", "/api/v1/roles/**").hasRole("ADMIN")

                        // Append-only clinical audit trail
                        .requestMatchers(HttpMethod.POST, "/api/v1/audit-logs/**").hasAnyRole("NURSE", "DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/audit-logs/**").hasAnyRole("DOCTOR", "ADMIN")

                        // Patients
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/patients/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/patients/**").hasAnyRole("NURSE", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/patients/**").hasAnyRole("NURSE", "DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/patients/**").hasAnyRole("NURSE", "DOCTOR", "ADMIN")

                        // Vital signs
                        .requestMatchers(HttpMethod.POST, "/api/v1/vital-sign-records/**").hasAnyRole("NURSE", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/vital-sign-records/**").hasAnyRole("NURSE", "DOCTOR", "ADMIN")

                        // Operational clinical events
                        .requestMatchers(HttpMethod.POST, "/api/v1/clinical-events/**").hasAnyRole("NURSE", "DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/clinical-events/**").hasAnyRole("NURSE", "DOCTOR", "ADMIN")

                        // Nursing handovers
                        .requestMatchers(HttpMethod.POST, "/api/v1/handovers/**").hasAnyRole("NURSE", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/handovers/**").hasAnyRole("NURSE", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/handovers/**").hasAnyRole("NURSE", "DOCTOR", "ADMIN")

                        // Clinical alerts
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/alerts/*/close").hasAnyRole("DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/alerts/*/attend").hasAnyRole("NURSE", "DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/alerts/**").hasAnyRole("NURSE", "DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/alerts/**").hasAnyRole("NURSE", "DOCTOR", "ADMIN")

                        // Any future API endpoint remains restricted to known application roles.
                        .requestMatchers("/api/v1/**").hasAnyRole("NURSE", "DOCTOR", "ADMIN")
                        .anyRequest().authenticated());
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authorizationRequestFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();

    }

    /**
     * This is the constructor of the class.
     * @param userDetailsService The user details service
     * @param tokenService The token service
     * @param hashingService The hashing service
     * @param authenticationEntryPoint The authentication entry point
     */
    public WebSecurityConfiguration(@Qualifier("defaultUserDetailsService") UserDetailsService userDetailsService, BearerTokenService tokenService, BCryptHashingService hashingService, AuthenticationEntryPoint authenticationEntryPoint, AccessDeniedHandler accessDeniedHandler) {
        this.userDetailsService = userDetailsService;
        this.tokenService = tokenService;
        this.hashingService = hashingService;
        this.unauthorizedRequestHandler = authenticationEntryPoint;
        this.forbiddenRequestHandler = accessDeniedHandler;
    }
}
