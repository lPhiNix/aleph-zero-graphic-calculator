package com.placeholder.placeholder.api.math.dto.response;

import com.placeholder.placeholder.api.math.enums.computation.MathExpressionType;

import java.util.List;

/**
 * Data Transfer Object representing a mathematical expression
 * along with its identified type and a list of evaluation results.
 *
 * @param expression The original mathematical expression string
 * @param type The determined type of the expression (e.g., FUNCTION, ASSIGNMENT)
 * @param evaluations List of evaluation results for this expression
 */
public record MathExpressionEvaluationDto(
        String expression,
        MathExpressionType type,
        List<MathEvaluationDto> evaluations
) {}
