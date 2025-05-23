package com.placeholder.placeholder.api.math.service.strategy.strategies;

import com.placeholder.placeholder.api.math.dto.request.MathDataDto;
import com.placeholder.placeholder.api.math.dto.response.MathEvaluationDto;
import com.placeholder.placeholder.api.math.service.core.MathCachedEvaluationService;
import com.placeholder.placeholder.api.math.service.strategy.EvaluationStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AssignmentEvaluationStrategy implements EvaluationStrategy {
    @Override
    public List<MathEvaluationDto> compute(String expression, MathDataDto data) {
        return List.of();
    }

    @Override
    public MathCachedEvaluationService getEvaluatorService() {
        return null;
    }
}
