package com.placeholder.placeholder.api.math.exceptions;

public class MathSyntaxException extends MathException {
    public MathSyntaxException(String message) {
        super(message);
    }

    public MathSyntaxException() {
        super("Syntax error in expression.");
    }
}
