package com.placeholder.placeholder.api.util.common.auth.internal;

import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Key;
import java.util.Base64;
import java.util.UUID;

@Configuration
@ConditionalOnProperty(name = "jwt.provider", havingValue = "internal", matchIfMissing = true)
public class InternalJwtConfig {
    private static final Logger logger = LoggerFactory.getLogger(InternalJwtConfig.class);

    @Value("${jwt.secret}")
    private String jwtSecret;
    private final Key secretKey;

    public InternalJwtConfig() {
        if (jwtSecret == null || jwtSecret.isBlank()) {
            logger.warn("{jwt.secret} is null or empty, a random secret key will be generated");
            jwtSecret = Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
        }
        secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret));
    }

    /**
     * Getter for the secret key
     * @return generated or assigned secret key.
     */
    @Bean
    public Key getSecretKey() {
        return secretKey;
    }
}
