package com.placeholder.placeholder.api.util.common.auth.base;

import java.util.Map;

/**
 * Use this when you want to make an implementation of {@link JwtFacade} which handles and generates
 * its own JWT tokens.
 * <p>
 * is possible to make multiple token generators with different algorithms
 * if needed.
 * </p>
 */
public interface JWTTokenGenerator {
    /**
     * Generates a valid JWT token containing information from users: {@link com.placeholder.placeholder.db.models.User}
     *
     * @param subject the user identifier (email)
     * @param claims map with useful information embedded within the token (name, role, expiration date, etc.).
     * @return a valid JWT token for the requested user.
     */
    String generateToken(String subject, Map<String, Object> claims);

    /**
     * Optional: default expiration time (ms) for tokens generated, 10 hours.
     */
    default long getDefaultExpirationMillis() {
        return 1000 * 60 * 60 * 10;
    }
}
