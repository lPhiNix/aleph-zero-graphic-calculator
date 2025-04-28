package com.placeholder.placeholder.util.messages.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.placeholder.placeholder.util.messages.dto.content.MessageContent;

import java.util.List;

/**
 * Standard DTO response message for errors
 * @param detailedMessage a detailed description of the error.
 * @param simpleMessage a simplified version of the error.
 * @param errors optional value, contains a list of {@link ErrorDetail} for more information
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String detailedMessage,
        String simpleMessage,
        List<ErrorDetail> errors
) implements MessageContent
{
    @Override
    public String toString() {
        return "ErrorResponse{" +
                "detailedMessage='" + detailedMessage + '\'' +
                ", simpleMessage='" + simpleMessage + '\'' +
                ", errors=" + errors +
                '}';
    }
}