package com.alephzero.alephzero.api.v1.auth.dto;

/**
 * Authentication response body.
 * <p>
 * Only the access token is returned here: the refresh token travels exclusively
 * in an HttpOnly cookie and is never exposed to JavaScript.
 *
 * @param accessToken the short-lived signed JWT
 * @param tokenType   always {@code Bearer}
 * @param expiresIn   access token lifetime in seconds
 */
public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
    public AuthResponse(String accessToken, long expiresIn) {
        this(accessToken, "Bearer", expiresIn);
    }
}
