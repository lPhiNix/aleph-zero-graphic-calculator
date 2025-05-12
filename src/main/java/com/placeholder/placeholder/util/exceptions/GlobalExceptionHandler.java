package com.placeholder.placeholder.util.exceptions;

import com.placeholder.placeholder.util.enums.AppCode;
import com.placeholder.placeholder.util.messages.ApiResponseFactory;
import com.placeholder.placeholder.util.messages.dto.ApiResponse;
import com.placeholder.placeholder.util.messages.dto.error.ErrorCategory;
import com.placeholder.placeholder.util.messages.dto.error.details.ErrorDetail;
import com.placeholder.placeholder.util.messages.dto.error.details.ValidationErrorDetail;
import com.placeholder.placeholder.util.messages.dto.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;


@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {

    public static final String DEFAULT_ERROR_MESSAGE = "An unexpected error occurred";

    private final ApiResponseFactory responseFactory;

    public GlobalExceptionHandler(ApiResponseFactory responseFactory) {
        this.responseFactory = responseFactory;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleAllExceptions(
            Exception ex
    ) {
        return responseFactory.error(
                AppCode.INTERNAL_ERROR,
                DEFAULT_ERROR_MESSAGE,
                ex.getMessage(),
                List.of(new ErrorDetail(ErrorCategory.INTERNAL, ex.getCause().getMessage(), DEFAULT_ERROR_MESSAGE))
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleRuntimeException(
            RuntimeException ex
    ) {
        return responseFactory.error(
                AppCode.INTERNAL_ERROR,
                "Runtime error",
                ex.getMessage(),
                List.of(new ErrorDetail(ErrorCategory.INTERNAL, ex.getCause().getMessage(), DEFAULT_ERROR_MESSAGE))
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleIllegalArgumentException(
            IllegalArgumentException ex
    ) {
        return responseFactory.error(
                AppCode.BAD_REQUEST,
                "Invalid argument",
                ex.getMessage(),
                List.of(new ErrorDetail(ErrorCategory.INTERNAL, ex.getCause().getMessage(), DEFAULT_ERROR_MESSAGE))
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleIllegalStateException(
            IllegalStateException ex
    ) {
        return responseFactory.error(
                AppCode.CONFLICT,
                "Illegal state",
                ex.getMessage(),
                List.of(new ErrorDetail(ErrorCategory.INTERNAL, ex.getCause().getMessage(), DEFAULT_ERROR_MESSAGE))
        );
    }

    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleTypeMismatchException(
            TypeMismatchException ex,
            HttpServletRequest request
    ) {
        String param = ex.getPropertyName();
        String expectedType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String message = String.format("Invalid value for parameter '%s': expected type %s", param, expectedType);

        ValidationErrorDetail detail = new ValidationErrorDetail(
                ErrorCategory.VALIDATION,
                param,
                message,
                ex.getValue()
        );

        return responseFactory.error(
                AppCode.BAD_REQUEST,
                "Type mismatch",
                message,
                List.of(detail)
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex
    ) {
        ex.getMostSpecificCause();
        String cause = ex.getMostSpecificCause().getMessage();

        ErrorDetail detail = new ErrorDetail(ErrorCategory.VALIDATION, cause, DEFAULT_ERROR_MESSAGE);

        return responseFactory.error(
                AppCode.BAD_REQUEST,
                "Malformed request body",
                cause,
                List.of(detail)
        );
    }
}

