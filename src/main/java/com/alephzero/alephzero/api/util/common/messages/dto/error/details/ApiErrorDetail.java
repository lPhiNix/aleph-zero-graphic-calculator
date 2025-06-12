package com.alephzero.alephzero.api.util.common.messages.dto.error.details;

import com.alephzero.alephzero.api.util.common.messages.dto.error.ErrorCategory;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ValidationErrorDetail.class, name = "validation")
})
public interface ApiErrorDetail {
    ErrorCategory category();
    String message();
}
