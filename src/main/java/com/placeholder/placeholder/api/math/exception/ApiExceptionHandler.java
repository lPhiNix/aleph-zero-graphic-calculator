package com.placeholder.placeholder.api.math.exception;

import com.placeholder.placeholder.api.util.common.messages.ApiResponseFactory;
import com.placeholder.placeholder.api.util.common.messages.dto.error.ErrorCategory;
import com.placeholder.placeholder.api.util.common.messages.dto.error.details.ErrorDetail;
import com.placeholder.placeholder.api.util.common.messages.dto.error.ErrorResponse;
import com.placeholder.placeholder.util.config.enums.AppCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Global exception handler for the application.
 * Provides centralized handling of exceptions thrown by controllers.
 */
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class ApiExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    private final ApiResponseFactory responseFactory;

    public ApiExceptionHandler(ApiResponseFactory responseFactory) {
        this.responseFactory = responseFactory;
    }

    /**
     * Handles MathEvaluationTimeoutException specifically.
     *
     * @param ex the exception instance
     * @return a ResponseEntity with an error response indicating a timeout error
     */
    @ExceptionHandler(MathEvaluationTimeoutException.class)
    public ResponseEntity<ErrorResponse> handleMathEvaluationTimeoutException(MathEvaluationTimeoutException ex) {
        logger.error("Math evaluation timed out: {}", ex.getMessage(), ex);
        return responseFactory.error(
                AppCode.TIMEOUT,
                "Timeout during math evaluation",
                List.of(new ErrorDetail(
                        ErrorCategory.INTERNAL,
                        ex.getMessage(),
                        "The evaluation of the mathematical expression exceeded the maximum allowed time."
                ))
        );
    }
}
