package com.placeholder.placeholder.util.enums;

import org.springframework.http.HttpStatus;

public enum AppCode {
    OK(HttpStatus.OK, "Operation Successful"),
    CREATED(HttpStatus.CREATED, "Resource created successfully"),
    NO_CONTENT(HttpStatus.NO_CONTENT, "No Content available"),

    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad request"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, " Unauthorized"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Access forbidden"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not found"),
    CONFLICT(HttpStatus.CONFLICT, "Resource conflict"),

    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable"),

    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Validation Error"),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "Entity not found"),
    DUPLICATE_ENTITY(HttpStatus.CONFLICT, " Duplicate entity"),;

    private final HttpStatus status;
    private final String simpleMessage;

    AppCode(HttpStatus httpStatus, String simpleMessage) {
        this.status = httpStatus;
        this.simpleMessage = simpleMessage;
    }

    public String value() {
        return name();
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getSimpleMessage() {
        return simpleMessage;
    }
}

