package com.placeholder.placeholder.api.math.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.placeholder.placeholder.api.util.common.messages.dto.content.MessageContent;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExpressionResultResponse(
    String lateXResultEvaluation,
    String points
) implements MessageContent {
    public ExpressionResultResponse(String lateXResultEvaluation) {
        this(lateXResultEvaluation, null);
    }
}
