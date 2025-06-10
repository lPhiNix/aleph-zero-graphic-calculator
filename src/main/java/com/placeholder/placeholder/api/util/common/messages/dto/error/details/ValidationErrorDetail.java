package com.placeholder.placeholder.api.util.common.messages.dto.error.details;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.placeholder.placeholder.api.util.common.messages.dto.error.ErrorCategory;

/**
 * Standard DTO model for error details
 * @param category enum of type {@link ErrorCategory}, defines the category of the error for better identification.
 * @param field the field where the problem has occurred
 * @param message details of the problem
 * @param rejectedValue the value, which has been rejected
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ValidationErrorDetail(
        ErrorCategory category,
        String field,
        String message,
        Object rejectedValue
) implements ApiErrorDetail {}