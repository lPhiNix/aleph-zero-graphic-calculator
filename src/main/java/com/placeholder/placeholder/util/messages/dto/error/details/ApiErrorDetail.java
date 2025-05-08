package com.placeholder.placeholder.util.messages.dto.error.details;

import com.placeholder.placeholder.util.messages.dto.error.ErrorCategory;

public interface ApiErrorDetail {
    ErrorCategory category();
    String message();
}
