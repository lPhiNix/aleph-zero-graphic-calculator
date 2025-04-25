package com.placeholder.placeholder.util.messages;

import com.placeholder.placeholder.util.enums.AppCode;
import com.placeholder.placeholder.util.messages.builders.ApiResponseBuilder;
import com.placeholder.placeholder.util.messages.dto.ApiMessage;
import com.placeholder.placeholder.util.messages.dto.MessageContent;
import com.placeholder.placeholder.util.messages.dto.error.ErrorDetail;
import com.placeholder.placeholder.util.messages.dto.error.ErrorResponse;

import java.util.List;

public class ApiResponseFactory {

    private final static ApiMessageContentFactory apiMessageContentFactory;

    static {
        apiMessageContentFactory = new ApiMessageContentFactory();
    }

    private static  <T extends MessageContent>ApiMessage<T> createApiMessage(String path, AppCode code, String message, T content) {
        return ApiResponseBuilder.<T>builder()
                .status(code.getStatus().value())
                .code(code.value())
                .path(path)
                .message(message)
                .content(content)
                .build();
    }

    public static ApiMessage<ErrorResponse> createErrorResponse(String path, AppCode code, String details){
        return createApiMessage(path, code, null, apiMessageContentFactory.getErrorResponse(code.getSimpleMessage(), details));
    }

    public static ApiMessage<ErrorResponse> createErrorResponse(String path, AppCode code, String details, List<ErrorDetail> errors){
        return createApiMessage(path, code, null, apiMessageContentFactory.getErrorResponseWithErrors(code.getSimpleMessage(), details, errors));
    }
}