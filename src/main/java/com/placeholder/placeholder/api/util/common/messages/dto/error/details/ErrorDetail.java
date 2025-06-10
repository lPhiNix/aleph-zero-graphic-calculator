package com.placeholder.placeholder.api.util.common.messages.dto.error.details;

import com.placeholder.placeholder.api.util.common.messages.dto.error.ErrorCategory;

public record ErrorDetail(
        ErrorCategory category,
        String cause,
        String message
) implements ApiErrorDetail {
}
