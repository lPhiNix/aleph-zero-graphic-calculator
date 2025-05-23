package com.placeholder.placeholder.api.math.service.strategy;

import com.placeholder.placeholder.api.math.dto.request.MathDataDto;
import com.placeholder.placeholder.api.math.dto.response.MathEvaluationDto;
import com.placeholder.placeholder.api.math.service.core.MathCachedEvaluationService;

import java.util.List;

public interface EvaluationStrategy {
    List<MathEvaluationDto> compute(String expression, MathDataDto data);

    MathCachedEvaluationService getEvaluatorService();

    default void stopRequestIfSupported() {
        MathCachedEvaluationService evaluatorService = getEvaluatorService();
        if (evaluatorService != null) {
            evaluatorService.stopRequest();
        }
    }
}