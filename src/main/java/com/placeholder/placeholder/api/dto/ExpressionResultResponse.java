package com.placeholder.placeholder.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.placeholder.placeholder.util.messages.dto.content.MessageContent;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExpressionResultResponse(
    String expression
) implements MessageContent {}
