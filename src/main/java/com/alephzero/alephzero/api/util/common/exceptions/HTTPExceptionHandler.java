package com.alephzero.alephzero.api.util.common.exceptions;

import com.alephzero.alephzero.util.config.enums.AppCode;
import com.alephzero.alephzero.api.util.common.messages.ApiMessageFactory;
import com.alephzero.alephzero.api.util.common.messages.dto.error.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Exception handler focused on handling HTTP-specific exceptions such as:
 * - Unsupported HTTP methods
 * - Unsupported or unacceptable media types
 * - Authentication and authorization errors
 * <p>
 * This handler is assigned an explicit order using {@link Order} to allow coordination
 * with other exception handlers in the system.
 * </p>
 */
@ControllerAdvice
@Order(3)
public class HTTPExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(HTTPExceptionHandler.class);

    private final ApiMessageFactory messageFactory;

    /**
     * Constructor for {@code HTTPExceptionHandler}.
     *
     * @param messageFactory The factory used to generate standardized error responses.
     */
    public HTTPExceptionHandler(ApiMessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    /**
     * Handles cases where the client uses an HTTP method (e.g., POST, PUT) that is not supported
     * by the requested endpoint (e.g., a GET-only route).
     *
     * @param ex Exception thrown when an unsupported method is used.
     * @return A {@link ResponseEntity} containing an {@link ErrorResponse} with METHOD_NOT_ALLOWED status.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex
    ) {
        logger.warn("Unsupported HTTP method: {}", ex.getMethod(), ex);
        return messageFactory.error()
                .code(AppCode.METHOD_NOT_ALLOWED)
                .summary(String.format("The HTTP method '%s' is not supported for this endpoint.", ex.getMethod()))
                .build();
    }

    /**
     * Handles situations where the client sends a request with a Content-Type that the server does not support,
     * such as sending "application/xml" to a JSON-only endpoint.
     *
     * @param ex Exception indicating the unsupported media type.
     * @return A {@link ResponseEntity} containing an {@link ErrorResponse} with UNSUPPORTED_MEDIA_TYPE status.
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMediaType(
            HttpMediaTypeNotSupportedException ex
    ) {
        logger.warn("Unsupported media type: {}", ex.getContentType(), ex);
        return messageFactory.error()
                .code(AppCode.UNSUPPORTED_MEDIA_TYPE)
                .summary("The specified media type is not supported.")
                .build();
    }

    /**
     * Handles cases where the server cannot generate a response in any of the formats
     * acceptable by the client (e.g., Accept: application/xml).
     *
     * @param ex Exception indicating the media type is not acceptable.
     * @return A {@link ResponseEntity} containing an {@link ErrorResponse} with NOT_ACCEPTABLE status.
     */
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotAcceptable(
            HttpMediaTypeNotAcceptableException ex
    ) {
        return messageFactory.error()
                .code(AppCode.NOT_ACCEPTABLE)
                .summary("The server cannot respond with a content type acceptable by the client.")
                .build();
    }
}