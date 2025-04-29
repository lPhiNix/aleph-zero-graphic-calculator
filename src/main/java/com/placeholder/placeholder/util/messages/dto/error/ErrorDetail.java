package com.placeholder.placeholder.util.messages.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Standard DTO model for error details
 * @param field the field where the problem has occurred
 * @param message details of the problem
 * @param rejectedValue the value, which has been rejected
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorDetail(
        String field,
        String message,
        Object rejectedValue
) {}