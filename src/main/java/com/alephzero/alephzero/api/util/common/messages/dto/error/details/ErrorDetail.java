package com.alephzero.alephzero.api.util.common.messages.dto.error.details;

import com.alephzero.alephzero.api.util.common.messages.dto.error.ErrorCategory;

public record ErrorDetail(
        ErrorCategory category,
        String cause,
        String message
) implements ApiErrorDetail {
}
