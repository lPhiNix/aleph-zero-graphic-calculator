package com.placeholder.placeholder.util.messages;

import com.placeholder.placeholder.util.messages.builders.ApiResponseBuilder;
import com.placeholder.placeholder.util.messages.builders.ErrorResponseBuilder;
import com.placeholder.placeholder.util.messages.dto.ApiMessage;
import com.placeholder.placeholder.util.messages.dto.error.ErrorResponse;

public class ApiResponseFactory {

    // example
    public ApiMessage<ErrorResponse> createErrorMessage(){
        return ApiResponseBuilder.<ErrorResponse>builder()
                .status(400)
                .code("TEST_ERROR")
                .message("Test message")
                .path("api/test")
                .content(ErrorResponseBuilder.builder()
                        .simpleMessage("Test message error simple")
                        .detailedMessage("test detailed error")
                        .errors(null) // no errors
                        .build()
                ).build();
    }
}
