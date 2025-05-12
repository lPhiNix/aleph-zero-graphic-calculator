package com.placeholder.placeholder.util.exceptions;

import com.placeholder.placeholder.util.enums.AppCode;
import com.placeholder.placeholder.util.messages.ApiResponseFactory;
import com.placeholder.placeholder.util.messages.dto.ApiResponse;
import com.placeholder.placeholder.util.messages.dto.error.ErrorCategory;
import com.placeholder.placeholder.util.messages.dto.error.details.ErrorDetail;
import com.placeholder.placeholder.util.messages.dto.error.ErrorResponse;
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

@ControllerAdvice
@Order(3)
public class HTTPExceptionHandler {

    private final ApiResponseFactory responseFactory;

    public HTTPExceptionHandler(ApiResponseFactory responseFactory) {
        this.responseFactory = responseFactory;
    }

    /**
     * Handles unsupported HTTP methods (e.g., POST used on a GET-only endpoint).
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex
    ) {
        String message = "HTTP method not supported: " + ex.getMethod();
        ErrorDetail detail = new ErrorDetail(ErrorCategory.INTERNAL, ex.getCause().getMessage(), message);

        return responseFactory.error(
                AppCode.METHOD_NOT_ALLOWED,
                message,
                ex.getMessage(),
                List.of(detail)
        );
    }

    /**
     * Handles cases where the Content-Type is not supported.
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleUnsupportedMediaType(
            HttpMediaTypeNotSupportedException ex
    ) {
        String message = "Unsupported media type: " + ex.getContentType();
        ErrorDetail detail = new ErrorDetail(ErrorCategory.INTERNAL, ex.getCause().getMessage(), message);

        return responseFactory.error(
                AppCode.UNSUPPORTED_MEDIA_TYPE,
                message,
                ex.getMessage(),
                List.of(detail)
        );
    }

    /**
     * Handles cases where no acceptable media type can be produced.
     */
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleMediaTypeNotAcceptable(
            HttpMediaTypeNotAcceptableException ex
    ) {
        String message = "Not acceptable media type";
        ErrorDetail detail = new ErrorDetail(ErrorCategory.INTERNAL, ex.getCause().getMessage(), message);

        return responseFactory.error(
                AppCode.NOT_ACCEPTABLE,
                message,
                ex.getMessage(),
                List.of(detail)
        );
    }

    /**
     * Handles access denied errors (e.g., insufficient roles/authorities).
     * This will be moved into a dedicated Auth Exception Handler.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleAccessDenied(
            AccessDeniedException ex
    ) {
        String message = "Access denied: insufficient permissions";
        ErrorDetail detail = new ErrorDetail(ErrorCategory.AUTHORIZATION, ex.getCause().getMessage() ,message);

        return responseFactory.error(
                AppCode.FORBIDDEN,
                message,
                ex.getMessage(),
                List.of(detail)
        );
    }

    /**
     * Handles authentication failures (e.g., invalid credentials, missing token).
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleAuthenticationException(
            AuthenticationException ex
    ) {
        String message = "Authentication failed: " + ex.getMessage();
        ErrorDetail detail = new ErrorDetail(ErrorCategory.AUTHENTICATION, ex.getCause().getMessage(), message);

        return responseFactory.error(
                AppCode.UNAUTHORIZED,
                message,
                ex.getMessage(),
                List.of(detail)
        );
    }
}

