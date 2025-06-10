package com.placeholder.placeholder.api.math.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.placeholder.placeholder.api.math.enums.computation.MathEvaluationType;

import java.util.List;

/**
 * Data Transfer Object representing the result of a mathematical evaluation.
 * <p>
 * Fields that are null will be omitted from the JSON response.
 * </p>
 *
 * @param evaluationType The type of evaluation performed (e.g., EVALUATION, CALCULATION, DRAWING)
 * @param evaluation The evaluated result as a string
 * @param evaluationProblems List of problems or warnings encountered during evaluation, if any
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MathEvaluationDto(
        MathEvaluationType evaluationType,
        String evaluation,
        List<String> evaluationProblems
) {}
