package com.alephzero.alephzero.api.math.dto.request;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Data Transfer Object representing a request for evaluating
 * one or multiple mathematical expressions along with additional data.
 * <p>
 * Uses nested validation for the list of expressions and the data object.
 * </p>
 *
 * @param expressions List of mathematical expressions to evaluate
 * @param data Additional parameters such as decimal precision and bounds
 */
public record MathEvaluationRequest(
        @Valid List<MathExpressionDto> expressions,
        @Valid MathDataDto data
) {}
