package com.placeholder.placeholder.api.math.service.strategy.strategies;

import com.placeholder.placeholder.api.math.dto.request.MathDataDto;
import com.placeholder.placeholder.api.math.dto.response.MathEvaluationDto;
import com.placeholder.placeholder.api.math.enums.computation.MathEvaluationType;
import com.placeholder.placeholder.api.math.facade.MathExpressionEvaluation;
import com.placeholder.placeholder.api.math.service.core.MathCachedEvaluator;
import com.placeholder.placeholder.api.math.service.strategy.EvaluationStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NumericEvaluationStrategy implements EvaluationStrategy {
    private final MathCachedEvaluator mathEvaluator;

    public NumericEvaluationStrategy(MathCachedEvaluator mathEvaluator) {
        this.mathEvaluator = mathEvaluator;
    }

    @Override
    public List<MathEvaluationDto> compute(String expression, MathDataDto data) {
        MathExpressionEvaluation evaluation = mathEvaluator.evaluate(expression);
        MathExpressionEvaluation calculation = mathEvaluator.calculate(expression, data);

        return List.of(
                new MathEvaluationDto(MathEvaluationType.EVALUATION,
                        evaluation.getExpressionEvaluated(),
                        evaluation.getEvaluationProblems().orElse(null)
                ),
                new MathEvaluationDto(MathEvaluationType.CALCULATION,
                        calculation.getExpressionEvaluated(),
                        calculation.getEvaluationProblems().orElse(null)
                )
        );
    }
}
