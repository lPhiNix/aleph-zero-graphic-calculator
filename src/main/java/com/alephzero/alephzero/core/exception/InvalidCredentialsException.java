package com.alephzero.alephzero.core.exception;

/**
 * Thrown when the provided credentials (username/email or password) are invalid.
 * Mapped to HTTP 401.
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Invalid username or password");
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
