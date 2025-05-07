package com.placeholder.placeholder.util.messages.dto.error.details;

import com.placeholder.placeholder.util.messages.dto.error.ErrorCategory;

public record ErrorDetail(
        ErrorCategory category,
        String cause,
        String message
) implements ApiErrorDetail {
}
