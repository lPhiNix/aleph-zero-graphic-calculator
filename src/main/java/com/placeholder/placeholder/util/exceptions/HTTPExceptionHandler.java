package com.placeholder.placeholder.util.exceptions;

import com.placeholder.placeholder.util.enums.AppCode;
import com.placeholder.placeholder.util.messages.ApiResponseUtils;
import com.placeholder.placeholder.util.messages.dto.ApiResponse;
import com.placeholder.placeholder.util.messages.dto.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class HTTPExceptionHandler {

    /**
     * Handles unsupported HTTP methods (e.g., POST used on a GET-only endpoint).
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request
    ) {
        AppCode code = AppCode.METHOD_NOT_ALLOWED;
        return ApiResponseUtils.buildErrorResponse(
                request.getRequestURI(),
                "HTTP method not supported: " + ex.getMethod(),
                code
        );
    }

    /**
     * Handles cases where the Content-Type is not supported.
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleUnsupportedMediaType(
            HttpMediaTypeNotSupportedException ex,
            HttpServletRequest request
    ) {
        AppCode code = AppCode.UNSUPPORTED_MEDIA_TYPE;
        return ApiResponseUtils.buildErrorResponse(
                request.getRequestURI(),
                "Unsupported media type: " + ex.getContentType(),
                code
        );
    }

    /**
     * Handles cases where no acceptable media type can be produced.
     */
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleMediaTypeNotAcceptable(
            HttpMediaTypeNotAcceptableException ex,
            HttpServletRequest request
    ) {
        AppCode code = AppCode.NOT_ACCEPTABLE;
        return ApiResponseUtils.buildErrorResponse(
                request.getRequestURI(),
                "Not acceptable media type",
                code
        );
    }

    /**
     * Handles access denied errors (e.g., insufficient roles/authorities).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {
        AppCode code = AppCode.FORBIDDEN;
        return ApiResponseUtils.buildErrorResponse(
                request.getRequestURI(),
                "Access denied: insufficient permissions",
                code
        );
    }

    /**
     * Handles authentication failures (e.g., invalid credentials, missing token).
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request
    ) {
        AppCode code = AppCode.UNAUTHORIZED;
        return ApiResponseUtils.buildErrorResponse(
                request.getRequestURI(),
                "Authentication failed: " + ex.getMessage(),
                code
        );
    }
}
