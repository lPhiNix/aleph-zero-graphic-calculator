package com.alephzero.alephzero.util.filters;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuration class for setting up CORS (Cross-Origin Resource Sharing) policies.
 * <p>
 * This configuration allows requests from the frontend URL specified in the application properties.
 * It supports common HTTP methods and allows credentials to be included in requests.
 * </p>
 */
@Configuration
public class CorsConfig {
    /**
     * The URL of the frontend application, which is allowed to make cross-origin requests.
     * This value is injected from the application properties file.
     */
    @Value("${FRONTEND_URL:http://localhost:5173}")
    private String frontendUrl;

    /**
     * Configures CORS settings for the application.
     * <p>
     * This method sets up allowed origins, methods, headers, and credentials for CORS requests.
     * It registers the configuration to apply to all endpoints (/**).
     * </p>
     *
     * @return a {@link CorsConfigurationSource} that defines the CORS configuration.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(frontendUrl));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}