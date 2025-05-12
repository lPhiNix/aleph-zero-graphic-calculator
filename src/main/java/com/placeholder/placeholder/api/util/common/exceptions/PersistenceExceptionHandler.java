package com.placeholder.placeholder.api.util.common.exceptions;

import com.placeholder.placeholder.util.enums.AppCode;
import com.placeholder.placeholder.api.util.common.messages.ApiResponseFactory;
import com.placeholder.placeholder.api.util.common.messages.dto.ApiResponse;
import com.placeholder.placeholder.api.util.common.messages.dto.error.ErrorCategory;
import com.placeholder.placeholder.api.util.common.messages.dto.error.details.ValidationErrorDetail;
import com.placeholder.placeholder.api.util.common.messages.dto.error.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.Objects;

@ControllerAdvice
@Order(2)
public class PersistenceExceptionHandler {

    private final ApiResponseFactory responseFactory;

    public PersistenceExceptionHandler(ApiResponseFactory responseFactory) {
        this.responseFactory = responseFactory;
    }

    /**
     * Handles database constraint violations, such as unique key violations.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex
    ) {
        String message = "Database integrity violation: " + Objects.requireNonNull(ex.getRootCause()).getMessage();
        ValidationErrorDetail detail = new ValidationErrorDetail(ErrorCategory.BUSINESS, "Database Constraint", message, ex.getRootCause());

        return responseFactory.error(
                AppCode.CONFLICT,
                message,
                ex.getMessage(),
                List.of(detail)
        );
    }

    /**
     * Handles cases where a requested entity was not found in the database.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleEntityNotFound(
            EntityNotFoundException ex
    ) {
        String message = "Entity not found: " + ex.getMessage();
        ValidationErrorDetail detail = new ValidationErrorDetail(ErrorCategory.NOT_FOUND, "Entity", message, null);

        return responseFactory.error(
                AppCode.NOT_FOUND,
                message,
                ex.getMessage(),
                List.of(detail)
        );
    }

    /**
     * Handles optimistic locking failures when concurrent updates conflict.
     */
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleOptimisticLockingFailure(
            OptimisticLockingFailureException ex
    ) {
        String message = "Optimistic locking failure: concurrent update conflict";
        ValidationErrorDetail detail = new ValidationErrorDetail(ErrorCategory.CONFLICT, "Optimistic Locking", message, null);

        return responseFactory.error(
                AppCode.CONFLICT,
                message,
                ex.getMessage(),
                List.of(detail)
        );
    }
}