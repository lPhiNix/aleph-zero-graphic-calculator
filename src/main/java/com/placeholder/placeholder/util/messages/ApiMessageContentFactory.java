package com.placeholder.placeholder.util.messages;

import com.placeholder.placeholder.util.enums.AppCode;
import com.placeholder.placeholder.util.messages.builders.ErrorResponseBuilder;
import com.placeholder.placeholder.util.messages.dto.SimpleResponse;
import com.placeholder.placeholder.util.messages.dto.error.ErrorDetail;
import com.placeholder.placeholder.util.messages.dto.error.ErrorResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory class for creating error responses
 */
public class ApiMessageContentFactory {
    private static final Map<AppCode, String> errorMessageMap = new HashMap<>();
    private static final Map<AppCode, String> messageMap = new HashMap<>();

    static {
        messageMap.put(AppCode.OK, "Operation successful.");
        messageMap.put(AppCode.CREATED, "Resource created successfully.");

        errorMessageMap.put(AppCode.BAD_REQUEST, "Bad request.");
        errorMessageMap.put(AppCode.UNAUTHORIZED, "Unauthorized.");
        errorMessageMap.put(AppCode.FORBIDDEN, "Access forbidden.");
        errorMessageMap.put(AppCode.NOT_FOUND, "Resource not found.");
        errorMessageMap.put(AppCode.CONFLICT, "Resource conflict.");
        errorMessageMap.put(AppCode.INTERNAL_ERROR, "Internal server error.");
        errorMessageMap.put(AppCode.SERVICE_UNAVAILABLE, "Service unavailable.");
        errorMessageMap.put(AppCode.VALIDATION_ERROR, "Validation error.");
        errorMessageMap.put(AppCode.ENTITY_NOT_FOUND, "Entity not found.");
        errorMessageMap.put(AppCode.DUPLICATE_ENTITY, "Duplicate entity.");
    }

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


    public ErrorResponse getErrorResponse(AppCode appCode, String detailedMessage) {
        String simpleMessage = errorMessageMap.get(appCode);
        return createErrorResponse(simpleMessage, detailedMessage);
    }

    public ErrorResponse getErrorResponseWithErrors(AppCode appCode, String detailedMessage, List<ErrorDetail> errors) {
        String simpleMessage = errorMessageMap.get(appCode);
        return createErrorResponse(simpleMessage, detailedMessage, errors);
    }
}
