package com.placeholder.placeholder.api.util.common.messages.dto.content;

/**
 * DTO model for a simple response
 * @param data embedded data
 */
public record SimpleResponse(
        String data
) implements MessageContent {
}
