package com.placeholder.placeholder.api.math.exceptions;

public class MathSemanticException extends MathException {
    public MathSemanticException(String message) {
        super(message);
    }

    public MathSemanticException() {
        super("Invalid function included.");
    }
}
