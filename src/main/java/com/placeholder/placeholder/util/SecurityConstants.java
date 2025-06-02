package com.placeholder.placeholder.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class SecurityConstants {
    @Bean
    public Set<String> allowedEndpoints() {
        return Set.of(
                "/api/public/**"
        );
    }

    @Bean
    Set<String> allowedStaticEndpoints() {
        return Set.of(
                "/styles/**",
                "/scripts/**"
        );
    }
}

