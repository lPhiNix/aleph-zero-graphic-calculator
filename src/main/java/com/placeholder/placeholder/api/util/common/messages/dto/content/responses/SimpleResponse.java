package com.placeholder.placeholder.api.util.common.messages.dto.content.responses;

import com.placeholder.placeholder.api.util.common.messages.dto.content.MessageContent;

/**
 * Simple DTO model for a simple response
 * @param data embedded data message.
 */
public record SimpleResponse(
        String data
) implements MessageContent {
}
