package com.placeholder.placeholder.api.math.dto.request;

import java.util.List;

public record MathEvaluationRequest(
        List<MathExpressionDto> expressions,
        MathDataDto data
) {}
