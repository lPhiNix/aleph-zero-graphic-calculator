package com.placeholder.placeholder.util.exceptions;

import com.placeholder.placeholder.util.enums.AppCode;
import com.placeholder.placeholder.util.messages.ApiResponseFactory;
import com.placeholder.placeholder.util.messages.dto.ApiResponse;
import com.placeholder.placeholder.util.messages.dto.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler that captures any uncaught exceptions
 * and returns a standardized error response.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Default error message used when no specific error information is available.
     */
    public static final String DEFAULT_ERROR_MESSAGE = "An unexpected error occurred";

    /**
     * Handles all uncaught exceptions and builds a standardized error response.
     *
     * @param ex      the thrown {@link Exception}
     * @param request the current {@link HttpServletRequest}
     * @return a {@link ResponseEntity} containing the error response with appropriate status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleAllExceptions(Exception ex, HttpServletRequest request) {
        AppCode code = AppCode.INTERNAL_ERROR;

        ApiResponse<ErrorResponse> errorMessage = ApiResponseFactory.createErrorResponse(
                request.getRequestURI(),
                code,
                DEFAULT_ERROR_MESSAGE + ": " + ex.getMessage()
        );

        return ResponseEntity.status(code.getStatus()).body(errorMessage);
    }

    /**
     * Handles any uncaught runtime exceptions.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleRuntimeException(
            RuntimeException ex,
            HttpServletRequest request
    ) {
        AppCode code = AppCode.INTERNAL_ERROR;
        ApiResponse<ErrorResponse> response = ApiResponseFactory.createErrorResponse(
                request.getRequestURI(),
                code,
                DEFAULT_ERROR_MESSAGE + ": " + ex.getMessage()
        );
        return ResponseEntity.status(code.getStatus()).body(response);
    }

    /**
     * Handles invalid arguments passed to a method.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        AppCode code = AppCode.BAD_REQUEST;
        ApiResponse<ErrorResponse> response = ApiResponseFactory.createErrorResponse(
                request.getRequestURI(),
                code,
                ex.getMessage()
        );
        return ResponseEntity.status(code.getStatus()).body(response);
    }

    /**
     * Handles illegal state of objects preventing an operation.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleIllegalStateException(
            IllegalStateException ex,
            HttpServletRequest request
    ) {
        AppCode code = AppCode.CONFLICT;
        ApiResponse<ErrorResponse> response = ApiResponseFactory.createErrorResponse(
                request.getRequestURI(),
                code,
                ex.getMessage()
        );
        return ResponseEntity.status(code.getStatus()).body(response);
    }
}
