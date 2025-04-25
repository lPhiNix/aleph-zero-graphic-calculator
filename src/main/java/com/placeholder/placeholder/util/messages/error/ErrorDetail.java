package com.placeholder.placeholder.util.messages.error;

/**
 * Standard DTO model for error details
 * @param field the field where the problem has occurred
 * @param message details of the problem
 * @param rejectedValue the value, which has been rejected
 */
public record ErrorDetail(
        String field,
        String message,
        Object rejectedValue
) {}