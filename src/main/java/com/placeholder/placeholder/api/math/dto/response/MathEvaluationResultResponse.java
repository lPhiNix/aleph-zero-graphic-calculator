package com.placeholder.placeholder.api.math.dto.response;

import com.placeholder.placeholder.api.util.common.messages.dto.content.MessageContent;

import java.util.List;

public record MathEvaluationResultResponse(
    List<MathExpressionEvaluationDto> expressionEvaluations
) implements MessageContent {}
