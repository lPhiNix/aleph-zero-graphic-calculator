package com.placeholder.placeholder.api.util.common.exceptions;

import com.placeholder.placeholder.api.util.common.messages.ApiResponseFactory;
import com.placeholder.placeholder.api.util.common.messages.ApiResponseUtils;
import com.placeholder.placeholder.api.util.common.messages.dto.ApiResponse;
import com.placeholder.placeholder.api.util.common.messages.dto.error.ErrorCategory;
import com.placeholder.placeholder.api.util.common.messages.dto.error.details.ValidationErrorDetail;
import com.placeholder.placeholder.api.util.common.messages.dto.error.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.validation.BindException;

import jakarta.validation.ConstraintViolationException;

import java.util.List;

/**
 * Global exception handler for validation-related exceptions.
 * <p>
 * This class captures and processes common validation exceptions
 * that occur during request handling, providing standardized error responses.
 */
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ValidationExceptionHandler {
    private final ApiResponseFactory apiResponseFactory;

    @Autowired
    public ValidationExceptionHandler(ApiResponseFactory apiResponseFactory) {
        this.apiResponseFactory = apiResponseFactory;
    }

    Logger log = LoggerFactory.getLogger(ValidationExceptionHandler.class);

    /**
     * Handles {@link MethodArgumentNotValidException} thrown when method arguments fail validation.
     *
     * @param ex      the exception instance
     * @return a standardized {@link ApiResponse} containing validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex
    ) {
        List<ValidationErrorDetail> validationErrorDetails = ApiResponseUtils.getErrorDetails(ex.getBindingResult(), ErrorCategory.VALIDATION);
        return apiResponseFactory.validationError(
                "Validation failed for one or more arguments, check your request.",
                validationErrorDetails
                );
    }

    /**
     * Handles {@link ConstraintViolationException} thrown when validation constraints are violated.
     *
     * @param ex      the exception instance
     * @return a standardized {@link ApiResponse} containing constraint violation details
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleConstraintViolationException(
            ConstraintViolationException ex
    ) {
        List<ValidationErrorDetail> validationErrorDetails = ApiResponseUtils.getErrorDetails(ex.getConstraintViolations(), ErrorCategory.VALIDATION);
        return apiResponseFactory.validationError(
                "Constraint violations detected.",
                validationErrorDetails
        );
    }

    /**
     * Handles {@link BindException} thrown when binding request parameters to an object fails.
     *
     * @param ex      the exception instance
     * @return a standardized {@link ApiResponse} containing data binding error details
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleBindException(
            BindException ex
    ) {
        List<ValidationErrorDetail> validationErrorDetails = ApiResponseUtils.getErrorDetails(ex.getBindingResult(), ErrorCategory.VALIDATION);
        return apiResponseFactory.validationError(
                "Data binding failed.",
                validationErrorDetails
        );
    }

    /**
     * Handles {@link MissingServletRequestParameterException} thrown when a required request parameter is missing.
     *
     * @param ex      the exception instance
     * @return a standardized {@link ApiResponse} containing information about the missing parameter
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex
    ) {
        List<ValidationErrorDetail> detail = List.of(new ValidationErrorDetail(ErrorCategory.VALIDATION, ex.getParameterName(), ex.getMessage(), null));
        return  apiResponseFactory.validationError(
                "Required request parameter is missing",
                detail
        );
    }
}