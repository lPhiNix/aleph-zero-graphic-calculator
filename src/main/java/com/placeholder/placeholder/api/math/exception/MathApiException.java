package com.placeholder.placeholder.api.math.exception;

public class MathApiException extends Throwable {
    public MathApiException(String message) {
        super(message);
    }

    public MathApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public MathApiException() {}
}
