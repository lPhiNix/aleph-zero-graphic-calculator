package com.placeholder.placeholder.api.util.common.mapper;

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
