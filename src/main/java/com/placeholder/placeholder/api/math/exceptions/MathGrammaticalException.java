package com.placeholder.placeholder.api.math.exceptions;

public class MathGrammaticalException extends MathException {
    public MathGrammaticalException(String message) {
        super(message);
    }

    public MathGrammaticalException() {
        super("Invalid variable name.");
    }
}
