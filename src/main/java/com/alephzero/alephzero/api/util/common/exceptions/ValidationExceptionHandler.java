package com.alephzero.alephzero.api.util.common.exceptions;

import com.alephzero.alephzero.api.util.common.messages.ApiMessageFactory;
import com.alephzero.alephzero.api.util.common.messages.ApiResponseUtils;
import com.alephzero.alephzero.api.util.common.messages.dto.error.ErrorCategory;
import com.alephzero.alephzero.api.util.common.messages.dto.error.details.ValidationErrorDetail;
import com.alephzero.alephzero.api.util.common.messages.dto.error.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Order(1)
@RequiredArgsConstructor
public class ValidationExceptionHandler {
    private final ApiMessageFactory messageFactory;


    Logger log = LoggerFactory.getLogger(ValidationExceptionHandler.class);

    /**
     * Handles {@link MethodArgumentNotValidException} thrown when method arguments fail validation.
     *
     * @param ex      the exception instance
     * @return a standardized {@link ErrorResponse} containing validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex
    ) {
        log.warn("Validation failed for method arguments: {}", ex.getMessage(), ex);
        List<ValidationErrorDetail> validationErrorDetails = ApiResponseUtils.getErrorDetails(ex.getBindingResult(), ErrorCategory.VALIDATION);
        return messageFactory.error()
                .validation("One or more fields in the request failed validation.")
                .title("Validation error in request body")
                .details(validationErrorDetails)
                .build();
    }

    /**
     * Handles {@link ConstraintViolationException} thrown when validation constraints are violated.
     *
     * @param ex      the exception instance
     * @return a standardized {@link ErrorResponse} containing constraint violation details
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex
    ) {
        log.warn("Constraint violations detected: {}", ex.getMessage(), ex);
        List<ValidationErrorDetail> validationErrorDetails = ApiResponseUtils.getErrorDetails(ex.getConstraintViolations(), ErrorCategory.VALIDATION);
        return messageFactory.error()
                .validation("Validation constraints were not met for one or more request parameters")
                .title("Validation error in request parameters")
                .details(validationErrorDetails)
                .build();
    }

    /**
     * Handles {@link BindException} thrown when binding request parameters to an object fails.
     *
     * @param ex      the exception instance
     * @return a standardized {@link ErrorResponse} containing data binding error details
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(
            BindException ex
    ) {
        log.warn("Data binding failed: {}", ex.getMessage(), ex);
        List<ValidationErrorDetail> validationErrorDetails = ApiResponseUtils.getErrorDetails(ex.getBindingResult(), ErrorCategory.VALIDATION);
        return messageFactory.error()
                .validation("Failed to bind request parameters to the target object.")
                .title("Data binding error")
                .details(validationErrorDetails)
                .build();
    }

    /**
     * Handles {@link MissingServletRequestParameterException} thrown when a required request parameter is missing.
     *
     * @param ex      the exception instance
     * @return a standardized {@link ErrorResponse} containing information about the missing parameter
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex
    ) {
        log.warn("Missing required request parameter: {}", ex.getParameterName(), ex);
        ValidationErrorDetail detail = new ValidationErrorDetail(ErrorCategory.VALIDATION, ex.getParameterName(), ex.getMessage(), "null");
        return messageFactory.error().validation(String.format("The required request parameter '%s' is missing.", ex.getParameterName()))
                .title("Missing request parameter")
                .detail(detail)
                .build();
    }
}