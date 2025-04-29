package com.placeholder.placeholder.util.exceptions;

import com.placeholder.placeholder.util.enums.AppCode;
import com.placeholder.placeholder.util.messages.ApiResponseFactory;
import com.placeholder.placeholder.util.messages.dto.ApiResponse;
import com.placeholder.placeholder.util.messages.dto.error.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

@ControllerAdvice
public class PersistenceExceptionHandler {

    /**
     * Handles database constraint violations, such as unique key violations.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        AppCode code = AppCode.CONFLICT;
        ApiResponse<ErrorResponse> response = ApiResponseFactory.createErrorResponse(
                request.getRequestURI(),
                code,
                "Database integrity violation: " + Objects.requireNonNull(ex.getRootCause()).getMessage()
        );
        return ResponseEntity.status(code.getStatus()).body(response);
    }

    /**
     * Handles cases where a requested entity was not found in the database.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleEntityNotFound(
            EntityNotFoundException ex,
            HttpServletRequest request
    ) {
        AppCode code = AppCode.NOT_FOUND;
        ApiResponse<ErrorResponse> response = ApiResponseFactory.createErrorResponse(
                request.getRequestURI(),
                code,
                "Entity not found: " + ex.getMessage()
        );
        return ResponseEntity.status(code.getStatus()).body(response);
    }

    /**
     * Handles optimistic locking failures when concurrent updates conflict.
     */
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleOptimisticLockingFailure(
            OptimisticLockingFailureException ex,
            HttpServletRequest request
    ) {
        AppCode code = AppCode.CONFLICT;
        ApiResponse<ErrorResponse> response = ApiResponseFactory.createErrorResponse(
                request.getRequestURI(),
                code,
                "Optimistic locking failure: concurrent update conflict"
        );
        return ResponseEntity.status(code.getStatus()).body(response);
    }
}