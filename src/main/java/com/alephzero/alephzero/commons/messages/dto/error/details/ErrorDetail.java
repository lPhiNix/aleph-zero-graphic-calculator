package com.alephzero.alephzero.commons.messages.dto.error.details;

import com.alephzero.alephzero.commons.messages.dto.error.ErrorCategory;

public record ErrorDetail(
        ErrorCategory category,
        String cause,
        String message
) implements ApiErrorDetail {
}
