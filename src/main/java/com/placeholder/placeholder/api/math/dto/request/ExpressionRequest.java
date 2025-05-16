package com.placeholder.placeholder.api.math.dto.request;

import java.util.List;

public record ExpressionRequest(
        List<MathExpression> expression,
        int decimals,
        String origin,
        String bound
) {}
