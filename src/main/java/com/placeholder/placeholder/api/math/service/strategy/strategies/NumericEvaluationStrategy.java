package com.placeholder.placeholder.api.math.service.strategy.strategies;

import com.placeholder.placeholder.api.math.dto.request.MathDataDto;
import com.placeholder.placeholder.api.math.dto.response.MathEvaluationDto;
import com.placeholder.placeholder.api.math.enums.MathEvaluationType;
import com.placeholder.placeholder.api.math.facade.MathExpressionEvaluation;
import com.placeholder.placeholder.api.math.service.micro.MathEvaluationCached;
import com.placeholder.placeholder.api.math.service.strategy.EvaluationStrategy;

import java.util.List;

public class NumericEvaluationStrategy implements EvaluationStrategy {
    private final MathEvaluationCached mathEvaluator;

    public NumericEvaluationStrategy(MathEvaluationCached mathEvaluator) {
        this.mathEvaluator = mathEvaluator;
    }

    @Override
    public List<MathEvaluationDto> compute(String expression, MathDataDto data) {
        MathExpressionEvaluation calculation = mathEvaluator.calculate(expression, data);

        return List.of(
                new MathEvaluationDto(MathEvaluationType.CALCULATION,
                        calculation.getExpressionEvaluated(),
                        calculation.getEvaluationProblems().orElse(null)
                )
        );
    }
}
