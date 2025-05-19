package com.placeholder.placeholder.api.math.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.placeholder.placeholder.api.math.enums.MathExpressionType;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MathEvaluationDto(
        MathExpressionType expressionType,
        String evaluation,
        List<String> evaluationProblems
) {}
