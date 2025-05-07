package com.placeholder.placeholder.util.exceptions;

import com.placeholder.placeholder.util.messages.ApiResponseUtils;
import com.placeholder.placeholder.util.messages.dto.ApiResponse;
import com.placeholder.placeholder.util.messages.dto.error.ErrorDetail;
import com.placeholder.placeholder.util.messages.dto.error.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.validation.BindException;

import jakarta.validation.ConstraintViolationException;

/**
 * Global exception handler for validation-related exceptions.
 * <p>
 * This class captures and processes common validation exceptions
 * that occur during request handling, providing standardized error responses.
 */
@ControllerAdvice
public class ValidationExceptionHandler {

    /**
     * Handles {@link MethodArgumentNotValidException} thrown when method arguments fail validation.
     *
     * @param ex      the exception instance
     * @param request the current HTTP request
     * @return a standardized {@link ApiResponse} containing validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<ErrorDetail> errorDetails = ApiResponseUtils.getErrorDetails(ex);
        return ApiResponseUtils.buildErrorResponse(
                request.getRequestURI(),
                "Validation failed. Please check your request.",
                errorDetails
        );
    }

    /**
     * Handles {@link ConstraintViolationException} thrown when validation constraints are violated.
     *
     * @param ex      the exception instance
     * @param request the current HTTP request
     * @return a standardized {@link ApiResponse} containing constraint violation details
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleConstraintViolationException(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        List<ErrorDetail> errorDetails = ApiResponseUtils.getErrorDetails(ex.getConstraintViolations());
        return ApiResponseUtils.buildErrorResponse(
                request.getRequestURI(),
                "Constraint violations detected.",
                errorDetails
        );
    }

    /**
     * Handles {@link BindException} thrown when binding request parameters to an object fails.
     *
     * @param ex      the exception instance
     * @param request the current HTTP request
     * @return a standardized {@link ApiResponse} containing data binding error details
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleBindException(
            BindException ex,
            HttpServletRequest request
    ) {
        List<ErrorDetail> errorDetails = ApiResponseUtils.getErrorDetails(ex);
        return ApiResponseUtils.buildErrorResponse(
                request.getRequestURI(),
                "Data binding failed.",
                errorDetails
        );
    }

    /**
     * Handles {@link MissingServletRequestParameterException} thrown when a required request parameter is missing.
     *
     * @param ex      the exception instance
     * @param request the current HTTP request
     * @return a standardized {@link ApiResponse} containing information about the missing parameter
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex,
            HttpServletRequest request
    ) {
        ErrorDetail errorDetail = ApiResponseUtils.createErrorDetail(
                ex.getParameterName(),
                "Required request parameter is missing"
        );

        return ApiResponseUtils.buildErrorResponse(
                request.getRequestURI(),
                "Missing request parameter.",
                List.of(errorDetail)
        );
    }
}