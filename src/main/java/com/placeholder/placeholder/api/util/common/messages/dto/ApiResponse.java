package com.placeholder.placeholder.api.util.common.messages.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.placeholder.placeholder.util.enums.AppCode;
import com.placeholder.placeholder.api.util.common.messages.dto.content.MessageContent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Standard DTO model for sending API responses with metadata
 * @param code APP status code (OK/SUCCESS, RESOURCE_NOT_FOUND, BAD_REQUEST…)
 * @param message optional message
 * @param timestamp timestamp containing the exact date and time of the creation
 * @param path endpoint URL
 * @param content the embedded content of the message, it can be either a class or a primitive value.
 * @param <T> generic value for the message content
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T extends MessageContent>(
        @NotNull String code,
        String message,
        LocalDateTime timestamp,
        String path,
        T content
)
{
    public ApiResponse(AppCode code, String message, String path, T content) {
        this(code.value(), message, LocalDateTime.now(), path, content);
    }

    @Override
    public String toString() {
        return "ApiMessage{" +
                ", code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", path='" + path + '\'' +
                ", content=" + content +
                '}';
    }
}