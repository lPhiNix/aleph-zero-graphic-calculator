package com.placeholder.placeholder.util.exceptions;

import com.placeholder.placeholder.util.enums.AppCode;
import com.placeholder.placeholder.util.messages.ApiResponseFactory;
import com.placeholder.placeholder.util.messages.dto.ApiMessage;
import com.placeholder.placeholder.util.messages.dto.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    public static final String DEFAULT_ERROR_MESSAGE = "An unexpected error occurred";

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiMessage<ErrorResponse>> handleAllExceptions(Exception ex, HttpServletRequest request) {
        AppCode code = AppCode.INTERNAL_ERROR;

        ApiMessage<ErrorResponse> errorMessage = ApiResponseFactory.createErrorResponse(
                request.getRequestURI(),
                code,
                DEFAULT_ERROR_MESSAGE + ": " + ex.getMessage()
        );
        return ResponseEntity.status(code.getStatus()).body(errorMessage);
    }
}
