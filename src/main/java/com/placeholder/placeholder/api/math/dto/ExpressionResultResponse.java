package com.placeholder.placeholder.api.math.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.placeholder.placeholder.api.util.common.messages.dto.content.MessageContent;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExpressionResultResponse(
    String lateXResultEvaluation,
    List<String> nativeErrorsList
) implements MessageContent {}
