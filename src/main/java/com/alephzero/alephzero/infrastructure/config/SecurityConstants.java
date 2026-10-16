package com.alephzero.alephzero.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * Configuration class holding all allowed paths for {@link com.alephzero.alephzero.infrastructure.security.CorsConfig} and
 * public paths for {@link com.alephzero.alephzero.infrastructure.security.SecurityConfig}
 */
@Configuration
public class SecurityConstants {
    /**
     * Public API endpoints that do not require authentication.
     * @return a {@link Set} of allowed endpoints.
     */
    @Bean
    public Set<String> allowedEndpoints() {
        return Set.of(
                "/api/v1/auth/**",
                "/api/public/**",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/swagger-resources/**",
                "/webjars/**"
        );
    }

    /**
     * Allowed static endpoints that do not require authentication.
     * These are typically for static resources like stylesheets, scripts, and images.
     * @return a {@link Set} of allowed static endpoints.
     */
    @Bean
    Set<String> allowedStaticEndpoints() {
        return Set.of(
                "/favicon.ico",
                "/favicon.png",
                "/favicons/**",
                "/assets/**",
                "/styles/**",
                "/scripts/**",
                "/snapshots/**",
                "/img/**",
                "/env.js",
                "/index.html"
        );
    }
}
