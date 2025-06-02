package com.placeholder.placeholder.api.util.common.exceptions;

import com.placeholder.placeholder.util.config.enums.AppCode;
import com.placeholder.placeholder.api.util.common.messages.ApiResponseFactory;
import com.placeholder.placeholder.api.util.common.messages.dto.error.ErrorCategory;
import com.placeholder.placeholder.api.util.common.messages.dto.error.details.ValidationErrorDetail;
import com.placeholder.placeholder.api.util.common.messages.dto.error.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.Objects;

/**
 * Exception handler for persistence-related exceptions.
 *<p>
 * This handler deals with typical database-layer problems such as:
 * - Violations of integrity constraints (e.g., unique keys, foreign keys).
 * - Missing entities during queries or deletions.
 * - Failures in optimistic locking due to concurrent modifications.
 *</p>
 * The use of {@link Order(2)} ensures this handler executes before lower-priority handlers
 * such as general exception handlers, but after more specific ones (e.g., validation).
 */
@ControllerAdvice
@Order(2)
public class PersistenceExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(PersistenceExceptionHandler.class);
    private final ApiResponseFactory responseFactory;

    /**
     * Constructor for {@code PersistenceExceptionHandler}.
     *
     * @param responseFactory The factory used to generate consistent and structured error responses.
     */
    public PersistenceExceptionHandler(ApiResponseFactory responseFactory) {
        this.responseFactory = responseFactory;
    }

    /**
     * Handles violations of database integrity constraints.
     *
     * Examples include:
     * - Inserting a duplicate value in a column marked as UNIQUE.
     * - Violating a NOT NULL constraint.
     * - Foreign key constraint violations.
     *
     * @param ex The exception thrown when a data integrity issue occurs.
     * @return A {@link ResponseEntity} containing a detailed {@link ErrorResponse} with CONFLICT status.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex
    ) {
        String rootMessage = Objects.requireNonNull(ex.getRootCause()).getMessage();
        String message = "Database integrity violation: " + rootMessage;

        ValidationErrorDetail detail = new ValidationErrorDetail(
                ErrorCategory.BUSINESS,
                "Database Constraint",
                message,
                ex.getRootCause()
        );

        return responseFactory.error(
                AppCode.CONFLICT,
                message,
                ex.getMessage(),
                List.of(detail)
        );
    }

    /**
     * Handles situations where a requested JPA entity does not exist in the database.
     *
     * Typically occurs in:
     * - Find-by-id operations where no record is found.
     * - Deletion or update attempts for non-existent entities.
     *
     * @param ex Exception thrown when an entity lookup fails.
     * @return A {@link ResponseEntity} containing a NOT_FOUND {@link ErrorResponse}.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(
            EntityNotFoundException ex
    ) {
        logger.warn("Entity not found: {}", ex.getMessage(), ex);
        String message = "Entity not found: " + ex.getMessage();

        ValidationErrorDetail detail = new ValidationErrorDetail(
                ErrorCategory.NOT_FOUND,
                "Entity",
                message,
                null
        );

        return responseFactory.error(
                AppCode.NOT_FOUND,
                message,
                ex.getMessage(),
                List.of(detail)
        );
    }

    /**
     * Handles optimistic locking failures.
     *
     * Occurs in scenarios where two or more users attempt to update the same entity concurrently,
     * and a version conflict is detected due to the use of an `@Version` field in JPA.
     *
     * Ensures that updates do not unintentionally overwrite each other.
     *
     * @param ex Exception indicating an optimistic lock conflict.
     * @return A {@link ResponseEntity} containing a CONFLICT {@link ErrorResponse}.
     */
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLockingFailure(
            OptimisticLockingFailureException ex
    ) {
        String message = "Optimistic locking failure: concurrent update conflict";

        ValidationErrorDetail detail = new ValidationErrorDetail(
                ErrorCategory.CONFLICT,
                "Optimistic Locking",
                message,
                null
        );

        return responseFactory.error(
                AppCode.CONFLICT,
                message,
                ex.getMessage(),
                List.of(detail)
        );
    }
}