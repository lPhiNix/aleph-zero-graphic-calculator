package com.alephzero.alephzero.infrastructure.security;

import com.alephzero.alephzero.core.services.auth.JWTService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Set;

/**
 * Security configuration for the stateless token-based authentication system.
 * <p>
 * A single filter chain protects the API ({@code /api/**}) and leaves the rest
 * (the SPA shell and its static assets, served by the same application in
 * production) publicly accessible. Access tokens are validated by
 * {@link JwtAuthenticationFilter}; there is no HTTP session, no OAuth2 and no
 * form login.
 */
@Profile("default")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Password encoder used to hash user passwords and to verify credentials at login.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the single security filter chain.
     *
     * @param http                     the {@link HttpSecurity} to configure
     * @param jwtService               the token service backing the authentication filter
     * @param allowedEndpoints         publicly accessible API endpoints
     * @param allowedStaticEndpoints   publicly accessible static resources
     * @param accessDeniedHandler      custom JSON 403 handler
     * @param authenticationEntryPoint custom JSON 401 handler
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JWTService jwtService,
                                                   Set<String> allowedEndpoints,
                                                   Set<String> allowedStaticEndpoints,
                                                   CustomAccessDeniedHandler accessDeniedHandler,
                                                   CustomAuthenticationEntryPoint authenticationEntryPoint) throws Exception {

        // Built manually (instead of as a @Component) so Spring Boot does not also
        // register it as a plain servlet filter outside the security chain.
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService);

        return http
                .csrf(AbstractHttpConfigurer::disable) // Stateless API: no CSRF token
                .cors(Customizer.withDefaults()) // CORS for the dev frontend origin
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    allowedEndpoints.forEach(endpoint -> auth.requestMatchers(endpoint).permitAll());
                    allowedStaticEndpoints.forEach(staticEndpoint -> auth.requestMatchers(staticEndpoint).permitAll());
                    auth.requestMatchers("/api/**").authenticated(); // Everything under /api requires a valid access token
                    auth.anyRequest().permitAll(); // SPA shell, assets and client-side routes
                })
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
