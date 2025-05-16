package com.placeholder.placeholder.api.util.common.messages.dto.content.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.placeholder.placeholder.api.util.common.messages.dto.content.MessageContent;

/**
 * Simple DTO response for creating {@link com.placeholder.placeholder.api.util.common.messages.dto.ApiResponse} instances with
 * empty content without the need of sending a null value.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record EmptyContentResponse() implements MessageContent {
}
