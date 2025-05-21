package com.placeholder.placeholder.api.math.service.strategy.strategies;

import com.placeholder.placeholder.api.math.dto.request.MathDataDto;
import com.placeholder.placeholder.api.math.dto.response.MathEvaluationDto;
import com.placeholder.placeholder.api.math.enums.MathEvaluationType;
import com.placeholder.placeholder.api.math.facade.MathExpressionEvaluation;
import com.placeholder.placeholder.api.math.service.core.MathEvaluationCached;
import com.placeholder.placeholder.api.math.service.strategy.EvaluationStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VectorEvaluationStrategy implements EvaluationStrategy {
    private final MathEvaluationCached mathEvaluator;

    public VectorEvaluationStrategy(MathEvaluationCached mathEvaluator) {
        this.mathEvaluator = mathEvaluator;
    }

    @Override
    public List<MathEvaluationDto> compute(String expression, MathDataDto data) {
        MathExpressionEvaluation evaluation = mathEvaluator.evaluate(expression);

        return List.of(
                new MathEvaluationDto(MathEvaluationType.EVALUATION,
                        evaluation.getExpressionEvaluated(),
                        evaluation.getEvaluationProblems().orElse(null)
                )
        );
    }
}
