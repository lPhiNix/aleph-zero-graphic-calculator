package com.placeholder.placeholder.api.math.exceptions;

import java.net.BindException;

public abstract class MathException extends BindException {
    public MathException(String message) {
        super(message);
    }
}
