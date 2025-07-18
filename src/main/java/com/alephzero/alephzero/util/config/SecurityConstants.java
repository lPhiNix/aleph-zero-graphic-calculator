package com.alephzero.alephzero.util.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * Configuration class holding all allowed paths for {@link com.alephzero.alephzero.util.filters.CorsConfig} and
 * public paths for {@link com.alephzero.alephzero.util.filters.SecurityConfig}
 */
@Configuration
public class SecurityConstants {
    /**
     * Allowed endpoints that do not require authentication.
     * @return a {@link Set} of allowed endpoints.
     */
    @Bean
    public Set<String> allowedEndpoints() {
        return Set.of(
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
                "/styles/**",
                "/scripts/**",
                "/snapshots/**",
                "/img/**"
        );
    }
}

