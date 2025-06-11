package com.alephzero.alephzero.api.util.common.messages.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.alephzero.alephzero.util.config.enums.AppCode;
import jakarta.validation.constraints.NotNull;

/**
 * Standard DTO model for sending API responses with metadata
 * @param code APP status code (OK/SUCCESS, RESOURCE_NOT_FOUND, BAD_REQUEST…)
 * @param message optional message
 * @param content the embedded content of the message, it can be either a class or a primitive value.
 * @param <T> generic value for the message content
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        @NotNull String code,
        String message,
        T content
)
{
    public ApiResponse(AppCode code, String message, T content) {
        this(code.value(), message, content);
    }
}