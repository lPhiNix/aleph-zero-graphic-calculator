package com.placeholder.placeholder.util.messages.builders;

import com.placeholder.placeholder.util.messages.dto.error.ErrorDetail;
import com.placeholder.placeholder.util.messages.dto.error.ErrorResponse;

import java.util.List;

public class ErrorResponseBuilder {
    private String detailedMessage;
    private String simpleMessage;
    private List<ErrorDetail> errors;

    public static ErrorResponseBuilder builder() {
        return new ErrorResponseBuilder();
    }

    public ErrorResponseBuilder detailedMessage(String detailedMessage) {
        this.detailedMessage = detailedMessage;
        return this;
    }

    public ErrorResponseBuilder simpleMessage(String simpleMessage) {
        this.simpleMessage = simpleMessage;
        return this;
    }

    public ErrorResponseBuilder errors(List<ErrorDetail> errors) {
        this.errors = errors;
        return this;
    }

    public ErrorResponse build() {
        return new ErrorResponse(detailedMessage, simpleMessage, errors);
    }
}
