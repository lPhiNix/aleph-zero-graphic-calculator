package com.placeholder.placeholder.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExpressionRequest(
        //@Validation
        String expression
) {}
