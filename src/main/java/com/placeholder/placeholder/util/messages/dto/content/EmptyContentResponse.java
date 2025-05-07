package com.placeholder.placeholder.util.messages.dto.content;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EmptyContentResponse() implements MessageContent {
}
