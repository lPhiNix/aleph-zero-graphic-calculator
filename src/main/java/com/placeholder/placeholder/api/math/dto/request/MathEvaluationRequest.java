package com.placeholder.placeholder.api.math.dto.request;

import jakarta.validation.Valid;

import java.util.List;

public record MathEvaluationRequest(
        @Valid List<MathExpressionDto> expressions,
        @Valid MathDataDto data
) {}