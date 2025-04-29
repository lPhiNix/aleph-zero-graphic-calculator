package com.placeholder.placeholder.util.exceptions;

import com.placeholder.placeholder.util.enums.AppCode;
import com.placeholder.placeholder.util.messages.ApiResponseUtils;
import com.placeholder.placeholder.util.messages.dto.ApiResponse;
import com.placeholder.placeholder.util.messages.dto.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleAllExceptions(
            Exception ex,
            HttpServletRequest request
    ) {
        AppCode code = AppCode.INTERNAL_ERROR;
        return ApiResponseUtils.buildErrorResponse(
                request.getRequestURI(),
                DEFAULT_ERROR_MESSAGE + ": " + ex.getMessage(),
                code
        );
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
        return ApiResponseUtils.buildErrorResponse(
                request.getRequestURI(),
                DEFAULT_ERROR_MESSAGE + ": " + ex.getMessage(),
                code
        );
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
        return ApiResponseUtils.buildErrorResponse(
                request.getRequestURI(),
                ex.getMessage(),
                code
        );
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
        return ApiResponseUtils.buildErrorResponse(
                request.getRequestURI(),
                ex.getMessage(),
                code
        );
    }

    /**
     * Handles type mismatch errors (e.g., when a String is passed but an Integer is expected).
     */
    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleTypeMismatchException(
            TypeMismatchException ex,
            HttpServletRequest request
    ) {
        AppCode code = AppCode.BAD_REQUEST;

        String message = String.format("Invalid value for parameter '%s': expected type %s",
                ex.getPropertyName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

        return ApiResponseUtils.buildErrorResponse(
                request.getRequestURI(),
                message,
                code
        );
    }

    /**
     * Handles unreadable request body (e.g., malformed JSON).
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        AppCode code = AppCode.BAD_REQUEST;

        String message = "Malformed request body: " + ex.getMostSpecificCause().getMessage();

        return ApiResponseUtils.buildErrorResponse(
                request.getRequestURI(),
                message,
                code
        );
    }
}