package com.placeholder.placeholder.util.config;

import com.placeholder.placeholder.api.util.common.auth.TokenClaims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.List;

/**
 * Configuration class for JWT settings.
 * It sets up the JWT secret key, expiration time, and JWT decoder with custom validators.
 */
@Configuration
public class JwtConfig {
    private static final Logger logger = LoggerFactory.getLogger(JwtConfig.class);
    private static final int DEFAULT_EXPIRATION_MS = 3600000; // 1 hour in milliseconds

    private final Key secretKey;
    private final long expirationMs;

    /**
     * Constructor that initializes the JWT secret key and expiration time.
     * If the secret is not provided, a random HS256 key is generated.
     *
     * @param secret       The JWT secret key as a string.
     * @param expirationMs The expiration time in milliseconds.
     */
    public JwtConfig(@Value("${jwt.secret}") String secret,
                     @Value("${jwt.expiration}") Long expirationMs) {
        // If the secret is null or empty, generate a random HS256 key
        if (secret == null || secret.isEmpty()) {
            logger.warn("JWT secret is not provided, a random HS256 key will be generated.");
            this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        } else {
            logger.info("Valid JWT secret provided, using it for signing.");
            this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        }
        this.expirationMs = (expirationMs > 0) ? expirationMs : DEFAULT_EXPIRATION_MS;
    }

    @Bean
    public Key jwtSecretKey() {
        return secretKey;
    }

    @Bean Long expirationMs() {
        return expirationMs;
    }

    /**
     * Creates a JwtDecoder bean that uses the configured secret key and applies custom validators.
     *
     * @return A JwtDecoder instance configured with the secret key and validators.
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withSecretKey((SecretKey) jwtSecretKey())
                .build();

        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer("placeholder");
        OAuth2TokenValidator<Jwt> withAudience = new JwtClaimValidator<List<String>>(
                TokenClaims.CLAIM_AUDIENCE, aud -> aud != null && aud.contains("expected-audience")
        );

        OAuth2TokenValidator<Jwt> hasRoleClaim = new JwtClaimValidator<>(
                TokenClaims.CLAIM_KEY_ROLE, role -> role != null && !role.toString().isEmpty()
        );

        // Combinar validadores
        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(
                withIssuer, withAudience, hasRoleClaim
        );

        decoder.setJwtValidator(validator);

        return decoder;
    }
}
