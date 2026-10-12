package com.alephzero.alephzero.commons.mapper;

public class MappingContextException extends RuntimeException {

    public MappingContextException(String message) {
        super(message);
    }

    public MappingContextException(String message, Throwable cause) {
        super(message, cause);
    }

    public MappingContextException(Throwable cause) {
        super(cause);
    }
}
