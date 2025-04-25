package com.placeholder.placeholder.util.messages;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String detailedMessage,
        List<ErrorDetail> errors
) {}