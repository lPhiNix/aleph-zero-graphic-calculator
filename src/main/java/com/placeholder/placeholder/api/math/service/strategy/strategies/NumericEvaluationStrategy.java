package com.placeholder.placeholder.api.math.service.strategy.strategies;

import com.placeholder.placeholder.api.math.dto.request.MathDataDto;
import com.placeholder.placeholder.api.math.dto.response.MathEvaluationDto;
import com.placeholder.placeholder.api.math.enums.computation.MathEvaluationType;
import com.placeholder.placeholder.api.math.facade.MathExpressionEvaluation;
import com.placeholder.placeholder.api.math.service.core.MathCachedEvaluationService;
import com.placeholder.placeholder.api.math.service.strategy.EvaluationStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * {@code NumericEvaluationStrategy} implements {@link EvaluationStrategy} to
 * provide numerical evaluation and calculation of mathematical expressions.
 * <p>
 * This strategy performs a basic evaluation of the expression and also calculates
 * the expression with provided input data, returning both results.
 * </p>
 */
@Component
public class NumericEvaluationStrategy implements EvaluationStrategy {

    private final MathCachedEvaluationService mathEvaluator;

    /**
     * Constructs a {@code NumericEvaluationStrategy} with the given evaluator service.
     *
     * @param mathEvaluator the cached evaluation service used to perform numeric computations
     */
    public NumericEvaluationStrategy(MathCachedEvaluationService mathEvaluator) {
        this.mathEvaluator = mathEvaluator;
    }

    /**
     * Computes both the evaluation and calculation of the given expression using input data.
     *
     * @param expression the mathematical expression to be evaluated and calculated
     * @param data       additional input data wrapped in {@link MathDataDto} for calculation context
     * @return a list containing two {@link MathEvaluationDto} instances: one for evaluation and one for calculation
     */
    @Override
    public List<MathEvaluationDto> compute(String expression, MathDataDto data) {
        MathExpressionEvaluation evaluation = mathEvaluator.evaluate(expression);
        MathExpressionEvaluation calculation = mathEvaluator.calculate(expression, data);

        formatOperations(mathEvaluator.getFacade(), evaluation, calculation);

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

    /**
     * Returns the cached evaluation service used by this strategy.
     *
     * @return the {@link MathCachedEvaluationService} instance
     */
    @Override
    public MathCachedEvaluationService getEvaluatorService() {
        return mathEvaluator;
    }
}
