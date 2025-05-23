package com.placeholder.placeholder.api.math.dto.response;

import com.placeholder.placeholder.api.util.common.messages.dto.content.MessageContent;

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
) implements MessageContent {}
