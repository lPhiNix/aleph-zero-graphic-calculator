package com.placeholder.placeholder.api.math.exceptions;

import com.placeholder.placeholder.api.util.common.messages.ApiResponseFactory;
import com.placeholder.placeholder.api.util.common.messages.dto.error.ErrorResponse;
import com.placeholder.placeholder.api.util.common.messages.dto.error.details.ApiErrorDetail;
import com.placeholder.placeholder.util.enums.AppCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MathApiExceptionGlobalHandler {
    private final ApiResponseFactory apiResponseFactory;

    @Autowired
    public MathApiExceptionGlobalHandler(ApiResponseFactory apiResponseFactory) {
        this.apiResponseFactory = apiResponseFactory;
    }

    @ExceptionHandler(MathGrammaticalException.class)
    public ResponseEntity<ErrorResponse> handleMathGrammaticalException(
            MathGrammaticalException ex
    ) {
        return apiResponseFactory.error(
                AppCode.VALIDATION_ERROR,
                ex.getMessage(),
                (List<ApiErrorDetail>) null
        );
    }

    @ExceptionHandler(MathSemanticException.class)
    public ResponseEntity<ErrorResponse> handleMathSemanticException(
            MathSemanticException ex
    ) {
        return apiResponseFactory.error(
                AppCode.VALIDATION_ERROR,
                ex.getMessage(),
                (List<ApiErrorDetail>) null
        );
    }

    @ExceptionHandler(MathSyntaxException.class)
    public ResponseEntity<ErrorResponse> handleMathSyntaxException(
            MathSyntaxException ex
    ) {
        return apiResponseFactory.error(
                AppCode.VALIDATION_ERROR,
                ex.getMessage(),
                (List<ApiErrorDetail>) null
        );
    }
}
