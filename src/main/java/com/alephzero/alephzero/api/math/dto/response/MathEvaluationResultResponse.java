package com.alephzero.alephzero.api.math.dto.response;

import java.io.Serializable;
import java.util.List;

/**
 * Data Transfer Object representing the overall response
 * containing a list of evaluated expressions.
 * <p>
 * Implements MessageContent to fit into the common messaging framework.
 *
 * @param expressionEvaluations List of evaluations for each submitted expression
 */
public record MathEvaluationResultResponse(
        List<MathExpressionEvaluationDto> expressionEvaluations
) implements Serializable {}
