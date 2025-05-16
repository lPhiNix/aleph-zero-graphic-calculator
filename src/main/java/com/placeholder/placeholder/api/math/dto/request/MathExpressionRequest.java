package com.placeholder.placeholder.api.math.dto.request;

import java.util.List;

public record MathExpressionRequest(
        List<MathExpression> expressions,
        int decimals,
        String origin,
        String bound
) {}
