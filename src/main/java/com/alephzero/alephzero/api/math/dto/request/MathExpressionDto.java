package com.alephzero.alephzero.api.math.dto.request;

import com.alephzero.alephzero.api.math.validation.symja.annotations.ValidMathEclipseExpression;

/**
 * Data Transfer Object representing a mathematical expression string
 * to be processed or evaluated.
 * <p>
 * The expression is validated with a custom validation annotation.
 * </p>
 *
 * @param expression The mathematical expression to evaluate
 */
public record MathExpressionDto(
        @ValidMathEclipseExpression String expression
) {}
