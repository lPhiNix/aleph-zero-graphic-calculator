package com.placeholder.placeholder.util.enums;

import org.springframework.http.HttpStatus;

public enum AppCode {
    OK(HttpStatus.OK),;

    private final HttpStatus status;
    AppCode(HttpStatus httpStatus) {
        this.status = httpStatus;
    }

    public String value(){
        return name();
    }

    public HttpStatus getStatus(){
        return status;
    }
}
