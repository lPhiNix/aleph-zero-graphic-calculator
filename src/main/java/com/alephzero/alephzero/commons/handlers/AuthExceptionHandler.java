package com.alephzero.alephzero.commons.handlers;

import com.alephzero.alephzero.commons.messages.ErrorResponseBuilder;
import com.alephzero.alephzero.commons.messages.dto.error.ErrorResponse;
import com.alephzero.alephzero.core.exception.InvalidCredentialsException;
import com.alephzero.alephzero.core.exception.InvalidRefreshTokenException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Exception handler for authentication failures. Returns HTTP 401 with the
 * standard {@link ErrorResponse} body instead of falling through to the generic
 * 500 handler.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ErrorResponseBuilder.unauthorized().title(ex.getMessage()).build();
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRefreshToken(InvalidRefreshTokenException ex) {
        return ErrorResponseBuilder.unauthorized().title(ex.getMessage()).build();
    }
}
