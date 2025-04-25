package com.placeholder.placeholder.util.enums;

import org.springframework.http.HttpStatus;

public enum AppCode {
    OK(HttpStatus.OK),
    CREATED(HttpStatus.CREATED),
    NO_CONTENT(HttpStatus.NO_CONTENT),

    BAD_REQUEST(HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED),
    FORBIDDEN(HttpStatus.FORBIDDEN),
    NOT_FOUND(HttpStatus.NOT_FOUND),
    CONFLICT(HttpStatus.CONFLICT),

    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE),

    VALIDATION_ERROR(HttpStatus.BAD_REQUEST),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND),
    DUPLICATE_ENTITY(HttpStatus.CONFLICT);

    private final HttpStatus status;

    AppCode(HttpStatus httpStatus) {
        this.status = httpStatus;
    }

    public String value() {
        return name();
    }

    public HttpStatus getStatus() {
        return status;
    }
}

