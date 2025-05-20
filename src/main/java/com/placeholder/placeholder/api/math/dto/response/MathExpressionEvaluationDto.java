package com.placeholder.placeholder.api.math.dto.response;

import com.placeholder.placeholder.api.math.enums.MathExpressionType;

import java.util.List;

public record MathExpressionEvaluationDto(
        String expression,
        MathExpressionType type,
        List<MathEvaluationDto> evaluations
) {
    public MathExpressionEvaluationDto(String expression) {
        this(expression, null, null);
    }
}
