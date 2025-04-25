package com.placeholder.placeholder.util.messages.dto;

public record SimpleResponse<T>(
        T data
) implements MessageContent {
}
