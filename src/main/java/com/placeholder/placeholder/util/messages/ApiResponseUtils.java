package com.placeholder.placeholder.util.messages;

import com.placeholder.placeholder.util.enums.AppCode;
import com.placeholder.placeholder.util.messages.dto.ApiResponse;
import com.placeholder.placeholder.util.messages.dto.error.ErrorDetail;
import com.placeholder.placeholder.util.messages.dto.error.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for building standardized API responses related to validation errors.
 */
public class ApiResponseUtils {

    /**
     * Builds a standardized error {@link ApiResponse} with validation error details.
     *
     * @param path          the request URI where the error occurred
     * @param message       the general error message
     * @param errorDetails  the list of specific validation errors
     * @return a {@link ResponseEntity} containing the error response with HTTP 400 (Bad Request) status
     */
    public static ResponseEntity<ApiResponse<ErrorResponse>> buildErrorResponse(
            String path,
            String message,
            List<ErrorDetail> errorDetails
    ) {
        ApiResponse<ErrorResponse> response = ApiResponseFactory.createErrorResponse(
                path,
                AppCode.VALIDATION_ERROR,
                message,
                errorDetails
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * Extracts {@link ErrorDetail} list from a {@link BindingResult} containing field validation errors.
     *
     * @param bindingResult the binding result containing validation errors
     * @return a list of {@link ErrorDetail} instances representing each validation error
     */
    public static List<ErrorDetail> getErrorDetails(BindingResult bindingResult) {
        return bindingResult
                .getFieldErrors()
                .stream()
                .map(fieldError -> new ErrorDetail(
                        fieldError.getField(),
                        fieldError.getDefaultMessage(),
                        fieldError.getRejectedValue()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Extracts {@link ErrorDetail} list from a set of {@link ConstraintViolation} instances.
     *
     * @param violations the set of constraint violations
     * @return a list of {@link ErrorDetail} instances representing each constraint violation
     */
    public static List<ErrorDetail> getErrorDetails(Set<ConstraintViolation<?>> violations) {
        return violations.stream()
                .map(violation -> new ErrorDetail(
                        violation.getPropertyPath().toString(),
                        violation.getMessage(),
                        violation.getInvalidValue()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Creates a single {@link ErrorDetail} for a missing or invalid request parameter.
     *
     * @param parameterName the name of the parameter
     * @param message       the associated error message
     * @return a new {@link ErrorDetail} instance
     */
    public static ErrorDetail createErrorDetail(String parameterName, String message) {
        return new ErrorDetail(parameterName, message, null);
    }
}