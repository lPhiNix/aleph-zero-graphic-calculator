package com.placeholder.placeholder.api.util.common.messages.dto.error.details;

import com.placeholder.placeholder.api.util.common.messages.dto.error.ErrorCategory;

public interface ApiErrorDetail {
    ErrorCategory category();
    String message();
}
