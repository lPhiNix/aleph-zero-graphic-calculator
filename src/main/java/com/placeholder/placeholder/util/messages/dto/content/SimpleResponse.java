package com.placeholder.placeholder.util.messages.dto.content;

/**
 * DTO model for a simple response
 * @param data embedded data
 * @param <T> type
 */
public record SimpleResponse<T>(
        T data
) implements MessageContent {
}
