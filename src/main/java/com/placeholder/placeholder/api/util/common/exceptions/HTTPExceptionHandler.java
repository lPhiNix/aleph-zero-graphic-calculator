package com.placeholder.placeholder.api.util.common.exceptions;

import com.placeholder.placeholder.util.config.enums.AppCode;
import com.placeholder.placeholder.api.util.common.messages.ApiResponseFactory;
import com.placeholder.placeholder.api.util.common.messages.dto.error.ErrorCategory;
import com.placeholder.placeholder.api.util.common.messages.dto.error.details.ErrorDetail;
import com.placeholder.placeholder.api.util.common.messages.dto.error.ErrorResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
/**
 * Exception handler focused on handling HTTP-specific exceptions such as:
 * - Unsupported HTTP methods
 * - Unsupported or unacceptable media types
 * - Authentication and authorization errors
 *<p>
 * This handler is assigned an explicit order using {@link Order} to allow coordination
 * with other exception handlers in the system.
 * </p>
 */
@ControllerAdvice
@Order(3)
public class HTTPExceptionHandler {

    private final ApiResponseFactory responseFactory;

    /**
     * Constructor for {@code HTTPExceptionHandler}.
     *
     * @param responseFactory The factory used to generate standardized error responses.
     */
    public HTTPExceptionHandler(ApiResponseFactory responseFactory) {
        this.responseFactory = responseFactory;
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
        return responseFactory.error(
                AppCode.METHOD_NOT_ALLOWED,
                "HTTP Method is not supported",
                ex.getMethod() + " is not allowed to perform this request"
        );
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
        String message = "Unsupported media type: " + ex.getContentType();
        ErrorDetail detail = new ErrorDetail(
                ErrorCategory.INTERNAL,
                (ex.getCause() != null) ? ex.getCause().getMessage() : message,
                message
        );

        return responseFactory.error(
                AppCode.UNSUPPORTED_MEDIA_TYPE,
                message,
                ex.getMessage(),
                List.of(detail)
        );
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
        String message = "Not acceptable media type";
        ErrorDetail detail = new ErrorDetail(
                ErrorCategory.INTERNAL,
                (ex.getCause() != null) ? ex.getCause().getMessage() : message,
                message
        );

        return responseFactory.error(
                AppCode.NOT_ACCEPTABLE,
                message,
                ex.getMessage(),
                List.of(detail)
        );
    }

    // THESE TWO WILL BE MOVED TO ANOTHER HANDLER

    /**
     * Handles authorization failures where the client is authenticated but does not have
     * sufficient privileges to access the requested resource (e.g., role restrictions).
     *
     * ⚠ This handler may later be extracted to a dedicated AuthExceptionHandler.
     *
     * @param ex Exception indicating access is denied.
     * @return A {@link ResponseEntity} containing an {@link ErrorResponse} with FORBIDDEN status.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex
    ) {
        String message = "Access denied: insufficient permissions";
        ErrorDetail detail = new ErrorDetail(
                ErrorCategory.AUTHORIZATION,
                (ex.getCause() != null) ? ex.getCause().getMessage() : message,
                message
        );

        return responseFactory.error(
                AppCode.FORBIDDEN,
                message,
                ex.getMessage(),
                List.of(detail)
        );
    }

    /**
     * Handles authentication failures, such as missing credentials, invalid tokens,
     * or incorrect username/password. This is distinct from access denial.
     *
     * @param ex Exception related to authentication problems.
     * @return A {@link ResponseEntity} containing an {@link ErrorResponse} with UNAUTHORIZED status.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex
    ) {
        String message = "Authentication failed: " + ex.getMessage();
        ErrorDetail detail = new ErrorDetail(
                ErrorCategory.AUTHENTICATION,
                (ex.getCause() != null) ? ex.getCause().getMessage() : message,
                message
        );

        return responseFactory.error(
                AppCode.UNAUTHORIZED,
                message,
                ex.getMessage(),
                List.of(detail)
        );
    }
}

