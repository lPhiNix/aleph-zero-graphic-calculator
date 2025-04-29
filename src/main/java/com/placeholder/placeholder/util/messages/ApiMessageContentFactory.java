package com.placeholder.placeholder.util.messages;

import com.placeholder.placeholder.util.messages.builders.ErrorResponseBuilder;
import com.placeholder.placeholder.util.messages.dto.error.ErrorDetail;
import com.placeholder.placeholder.util.messages.dto.error.ErrorResponse;

import java.util.List;

/**
 * Factory class for creating error responses
 */
public class ApiMessageContentFactory {
    private static ErrorResponse createErrorResponse(String simpleMessage, String detailedMessage, List<ErrorDetail> errors) {
        return ErrorResponseBuilder.builder()
                .detailedMessage(detailedMessage)
                .simpleMessage(simpleMessage)
                .errors(errors)
                .build();
    }

    public static ErrorResponse createErrorResponse(String simpleMessage, String detailedMessage) {
        return createErrorResponse(simpleMessage, detailedMessage, null);
    }

    public static ErrorResponse getErrorResponseWithErrors(String simpleMessage, String detailedMessage, List<ErrorDetail> errors) {
        return createErrorResponse(simpleMessage, detailedMessage, errors);
    }

    public static ErrorDetail createErrorDetail(String simpleMessage, String detailedMessage, Object rejectedValue) {
        return new ErrorDetail(simpleMessage, detailedMessage, rejectedValue);
    }

    public static ErrorDetail createErrorDetail(String simpleMessage, String detailedMessage) {
        return new ErrorDetail(simpleMessage, detailedMessage, null);
    }
}