package com.placeholder.placeholder.api.util.common.exceptions;

import com.placeholder.placeholder.api.util.common.messages.ApiMessageFactory;
import com.placeholder.placeholder.api.util.common.messages.ApiResponseFactory;
import com.placeholder.placeholder.api.util.common.messages.dto.error.ErrorCategory;
import com.placeholder.placeholder.api.util.common.messages.dto.error.details.ValidationErrorDetail;
import com.placeholder.placeholder.api.util.common.messages.dto.error.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for the application.  This class provides centralized handling for various
 * exceptions that may occur during the processing of a web request.  It is annotated with
 * {@link RestControllerAdvice}, which makes it applicable to all controllers in the application,
 * and {@link Order} to specify the order in which this handler is invoked.
 * <p>
 * In this case, {@link Ordered#LOWEST_PRECEDENCE} ensures that this handler is invoked last in the chain.
 * </p>
        */
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    /**
     * The default error message used when a more specific message is not available.
     */
    public static final String GENERIC_ERROR_MESSAGE = "An unexpected error occurred";
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    private final ApiResponseFactory responseFactory;
    private final ApiMessageFactory messageFactory;


    /**
     * Handles any uncaught exception.  This is the catch-all handler that deals with
     * exceptions not handled by any other specific handler.  It returns an
     * {@link ErrorResponse} with an internal server error code.
     *
     * @param ex The exception that was thrown.
     * @return A {@link ResponseEntity} containing an {@link ErrorResponse}.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        logger.error("Unhandled exception caught: {}", ex.getMessage(), ex);
        return messageFactory.error().internal(GENERIC_ERROR_MESSAGE).build();
    }

    /**
     * Handles {@link RuntimeException}.  Runtime exceptions are typically caused by programming errors.
     *
     * @param ex The {@link RuntimeException} that was thrown.
     * @return A {@link ResponseEntity} containing an {@link ErrorResponse}.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        logger.error("RuntimeException caught: {}", ex.getMessage(), ex);
        return messageFactory.error()
                .internal("An unexpected runtime error occurred during request processing")
                .build();
    }

    /**
     * Handles {@link IllegalArgumentException}.  This exception is thrown when a method argument is invalid.
     *
     * @param ex The {@link IllegalArgumentException} that was thrown.
     * @return A {@link ResponseEntity} containing an {@link ErrorResponse}.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("IllegalArgumentException caught: {}", ex.getMessage(), ex);
        return messageFactory.error()
                .badRequest("The request contains one or more invalid arguments. Please verify your input.")
                .build();
    }

    /**
     * Handles {@link IllegalStateException}.  This exception is thrown when a method is called at an illegal or
     * inappropriate time.
     *
     * @param ex The {@link IllegalStateException} that was thrown.
     * @return A {@link ResponseEntity} containing an {@link ErrorResponse}.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        logger.error("IllegalStateException caught: {}", ex.getMessage(), ex);
        return messageFactory.error()
                .conflict("The application encountered an invalid state while processing the request.")
                .build();
    }

    /**
     * Handles {@link TypeMismatchException}.  This exception is thrown when there is a type mismatch during
     * the binding of a property value.  For example, when a string is provided for an integer parameter.
     *
     * @param ex The {@link TypeMismatchException} that was thrown.
     * @return A {@link ResponseEntity} containing an {@link ErrorResponse}.
     */
    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(TypeMismatchException ex) {
        logger.info("TypeMismatchException caught: parameter='{}', value='{}', requiredType='{}'",
                ex.getPropertyName(), ex.getValue(), ex.getRequiredType());

        String param = ex.getPropertyName();
        String expectedType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String message = String.format("Invalid value for parameter '%s': expected type %s", param, expectedType);

        ValidationErrorDetail detail = new ValidationErrorDetail(
                ErrorCategory.VALIDATION,
                param,
                message,
                ex.getValue()
        );

        return messageFactory.error()
                .badRequest("Type mismatch")
                .detail(detail)
                .build();
    }


    /**
     * Handles {@link HttpMessageNotReadableException}. This exception is thrown when the HTTP message body is not readable,
     * typically due to a malformed request (e.g., invalid JSON).
     *
     * @param ex The {@link HttpMessageNotReadableException} that was thrown.
     * @return A {@link ResponseEntity} containing an {@link ErrorResponse}.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        logger.warn("HttpMessageNotReadableException caught: {}", ex.getMessage(), ex);
        return messageFactory.error()
                .badRequest("Message body not readable, request may be malformed")
                .build();
    }
}

