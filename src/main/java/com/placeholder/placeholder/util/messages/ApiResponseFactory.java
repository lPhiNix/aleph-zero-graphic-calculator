package com.placeholder.placeholder.util.messages;

import com.placeholder.placeholder.util.enums.AppCode;
import com.placeholder.placeholder.util.messages.builders.ApiResponseBuilder;
import com.placeholder.placeholder.util.messages.dto.ApiResponse;
import com.placeholder.placeholder.util.messages.dto.content.EmptyContentResponse;
import com.placeholder.placeholder.util.messages.dto.content.MessageContent;
import com.placeholder.placeholder.util.messages.dto.error.ErrorDetail;
import com.placeholder.placeholder.util.messages.dto.error.ErrorResponse;

import java.util.List;

public class ApiResponseFactory {

    private static  <T extends MessageContent> ApiResponse<T> createApiMessage(String path, AppCode code, String message, T content) {
        return ApiResponseBuilder.<T>builder()
                .status(code.getStatus().value())
                .code(code.value())
                .path(path)
                .message(message)
                .content(content)
                .build();
    }

    public static ApiResponse<ErrorResponse> createErrorResponse(String path, AppCode code, String details){
        return createApiMessage(path, code, null, ApiMessageContentFactory.createErrorResponse(code.getSimpleMessage(), details));
    }

    public static ApiResponse<ErrorResponse> createErrorResponse(String path, AppCode code, String details, List<ErrorDetail> errors){
        return createApiMessage(path, code, null, ApiMessageContentFactory.getErrorResponseWithErrors(code.getSimpleMessage(), details, errors));
    }

    public static ApiResponse<EmptyContentResponse> createEmptyContentResponse(String path, AppCode code, String message) {
        return createApiMessage(path, code, message, new EmptyContentResponse());
    }
}