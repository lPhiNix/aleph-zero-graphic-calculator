package com.placeholder.placeholder.api.math.dto.response;

import java.util.List;

public record ExpressionEvaluation(
        String expression,
        List<MathEvaluation> evaluations
) {}
