package com.alephzero.alephzero.core.exception;

/**
 * Thrown when a refresh token is missing, invalid, expired or revoked.
 * Mapped to HTTP 401.
 */
public class InvalidRefreshTokenException extends RuntimeException {

    public InvalidRefreshTokenException() {
        super("Invalid or expired refresh token");
    }

    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
