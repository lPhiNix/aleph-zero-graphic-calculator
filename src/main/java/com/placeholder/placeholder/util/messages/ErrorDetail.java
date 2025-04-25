package com.placeholder.placeholder.util.messages;

public record ErrorDetail(
        String field,
        String message,
        Object rejectedValue
) {}