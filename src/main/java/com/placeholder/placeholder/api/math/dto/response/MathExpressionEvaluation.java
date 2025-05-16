package com.placeholder.placeholder.api.math.dto.response;

import java.util.List;

public record MathExpressionEvaluation(
        String expression,
        List<MathEvaluation> evaluations
) {}
