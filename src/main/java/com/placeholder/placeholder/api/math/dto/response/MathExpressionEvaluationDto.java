package com.placeholder.placeholder.api.math.dto.response;

import java.util.List;

public record MathExpressionEvaluationDto(
        String expression,
        List<MathEvaluationDto> evaluations
) {
    public MathExpressionEvaluationDto(String expression) {
        this(expression, null);
    }
}
