package com.placeholder.placeholder.api.util.common.auth.base;

import java.util.Date;
import java.util.Map;

/**
 * This facade defines all common methods for creating JWTServices for authentication
 */
public interface JwtFacade {

    /**
     * Checks if the given token is valid:
     * <li>Has valid signature</li>
     * <li>Has not expired</li>
     * <li>Other conditions depending of your implementation.</li>
     * @param token JWT token to validate.
     * @return {@code true} if valid, otherwise {@code false}
     */
    boolean isValidToken(String token);

    /**
     * Extract the user identifier (email) from the token.
     * @param token JWT token.
     * @return the subject of the token (email).
     */
    String extractSubject(String token);

    /**
     * Extracts a claim from the token (a value identified with a key embedded in the token, IE:name, expiration date...).
     * @param token Valid JWT token.
     * @param key the name of the claim to extract.
     * @param clazz type of the claim.
     * @return the claim value (or null if it doesn't have one)
     */
    <T> T extractClaim(String token, String key, Class<T> clazz);

    /**
     * Extracts all claims from the token as a {@link Map}.
     * @param token JWT token.
     * @return a {@link Map} containing all pairs of key-values.
     */
    Map<String, Object> extractClaims(String token);

    /**
     * Extracts the expiration date from the claim
     * @param token JWT token
     * @return {@link Date} instance representing the expiration date.
     */
    Date extractExpiration(String token);
}
