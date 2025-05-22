package com.placeholder.placeholder.api.math.service.strategy;

import com.placeholder.placeholder.api.math.dto.request.MathDataDto;
import com.placeholder.placeholder.api.math.dto.response.MathEvaluationDto;

import java.util.List;

public interface EvaluationStrategy {
    List<MathEvaluationDto> compute(String expression, MathDataDto data);
}
