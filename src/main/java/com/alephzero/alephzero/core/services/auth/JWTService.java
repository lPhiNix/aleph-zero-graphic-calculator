package com.alephzero.alephzero.core.services.auth;

import com.alephzero.alephzero.core.models.User;
import com.alephzero.alephzero.infrastructure.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

/**
 * Single point of truth for every token operation in the application.
 * <p>
 * Encapsulates:
 * <ul>
 *     <li>Access tokens: signed JWTs (HS256) and their validation/parsing.</li>
 *     <li>Refresh tokens: opaque random values and their hashing for storage.</li>
 * </ul>
 * No other class should build, sign, parse or hash tokens directly.
 */
@Service
public class JWTService {

    /** Claim holding the username, kept for compatibility with the rest of the app. */
    public static final String CLAIM_USERNAME = "preferred_username";
    /** Claim holding the user email. */
    public static final String CLAIM_EMAIL = "email";
    /** Claim holding the user role name. */
    public static final String CLAIM_ROLE = "role";

    private static final int REFRESH_TOKEN_BYTES = 32; // 256 bits of entropy
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final JwtProperties jwtProperties;
    private final SecretKey signingKey;

    /**
     * Builds the HMAC signing key from the configured secret.
     * The secret is expected to be at least 32 bytes long (256 bits).
     */
    public JWTService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.signingKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a short-lived signed access token for the given user.
     *
     * @param user the authenticated user
     * @return a compact JWT string
     */
    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiration = now.plus(jwtProperties.getAccessTtl());

        return Jwts.builder()
                .subject(user.getPublicId())
                .claim(CLAIM_USERNAME, user.getUsername())
                .claim(CLAIM_EMAIL, user.getEmail())
                .claim(CLAIM_ROLE, user.getRole().getName())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(signingKey)
                .compact();
    }

    /**
     * Parses and verifies an access token.
     *
     * @param token the compact JWT
     * @return the verified claims
     * @throws JwtException if the token is invalid, malformed or expired
     */
    public Claims parseAccessToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * @param token the compact JWT
     * @return {@code true} if the token is valid and not expired
     */
    public boolean isAccessTokenValid(String token) {
        try {
            parseAccessToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * @param token the compact JWT
     * @return the username claim, or {@code null} if absent
     */
    public String extractUsername(String token) {
        return parseAccessToken(token).get(CLAIM_USERNAME, String.class);
    }

    /**
     * Generates a new opaque refresh token value.
     *
     * @return a URL-safe base64 random string
     */
    public String generateRefreshTokenValue() {
        byte[] bytes = new byte[REFRESH_TOKEN_BYTES];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Hashes a raw refresh token (SHA-256, hex) so the raw value is never stored.
     *
     * @param rawToken the raw refresh token
     * @return the hex-encoded hash
     */
    public String hashRefreshToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                hex.append(Character.forDigit((b >> 4) & 0xF, 16));
                hex.append(Character.forDigit(b & 0xF, 16));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is guaranteed by the JVM spec.
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    /** @return the configured access token lifetime. */
    public Duration getAccessTokenTtl() {
        return jwtProperties.getAccessTtl();
    }

    /** @return the configured refresh token lifetime. */
    public Duration getRefreshTokenTtl() {
        return jwtProperties.getRefreshTtl();
    }
}
