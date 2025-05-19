package com.placeholder.placeholder.api.math.dto.request;

import com.placeholder.placeholder.api.math.validation.symja.annotations.ValidMathEclipseExpression;

public record MathExpressionDto(
        @ValidMathEclipseExpression String expression
) {}
