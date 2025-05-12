package com.placeholder.placeholder.util.messages.dto.content;

/**
 * DTO model for a simple response
 * @param data embedded data
 */
public record SimpleResponse(
        String data
) implements MessageContent {
}
