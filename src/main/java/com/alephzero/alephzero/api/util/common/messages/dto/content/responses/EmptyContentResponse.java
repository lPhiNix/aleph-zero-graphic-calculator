package com.alephzero.alephzero.api.util.common.messages.dto.content.responses;

import com.alephzero.alephzero.api.util.common.messages.dto.ApiResponse;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * Simple DTO response for creating {@link ApiResponse} instances with
 * empty content without the need of sending a null value.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record EmptyContentResponse() implements Serializable {
}
