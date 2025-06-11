package com.alephzero.alephzero.api.util.common.messages.dto.error.details;

import com.alephzero.alephzero.api.util.common.messages.dto.error.ErrorCategory;

public interface ApiErrorDetail {
    ErrorCategory category();
    String message();
}
