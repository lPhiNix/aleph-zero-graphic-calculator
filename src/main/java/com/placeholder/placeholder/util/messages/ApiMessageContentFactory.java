package com.placeholder.placeholder.util.messages;

import com.placeholder.placeholder.util.messages.builders.ErrorResponseBuilder;
import com.placeholder.placeholder.util.messages.dto.SimpleResponse;
import com.placeholder.placeholder.util.messages.dto.error.ErrorDetail;
import com.placeholder.placeholder.util.messages.dto.error.ErrorResponse;

import java.util.List;

/**
 * Factory class for creating error responses
 */
public class ApiMessageContentFactory {
    private ErrorResponse createErrorResponse(String simpleMessage, String detailedMessage, List<ErrorDetail> errors) {
        return ErrorResponseBuilder.builder()
                .detailedMessage(detailedMessage)
                .simpleMessage(simpleMessage)
                .errors(errors)
                .build();
    }

    private ErrorResponse createErrorResponse(String simpleMessage, String detailedMessage) {
        return createErrorResponse(simpleMessage, detailedMessage, null);
    }

    public SimpleResponse<String> createSimpleStringResponse(String content) {
        return new SimpleResponse<>(content);
    }

    public ErrorResponse getErrorResponse(String simpleMessage, String detailedMessage) {
        return createErrorResponse(simpleMessage, detailedMessage);
    }

    public ErrorResponse getErrorResponseWithErrors(String simpleMessage, String detailedMessage, List<ErrorDetail> errors) {
        return createErrorResponse(simpleMessage, detailedMessage, errors);
    }
}