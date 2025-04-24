package com.placeholder.placeholder.api.exception;

public class PlaceholderApiException extends RuntimeException {
    public PlaceholderApiException(String message) {
        super(message);
    }

    public PlaceholderApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
