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
 * {@code MatrixEvaluationStrategy} implements the {@link EvaluationStrategy} interface
 * to handle the evaluation of matrix expressions.
 * <p>
 * It uses the cached evaluation service to compute the matrix evaluation and
 * returns the results in {@link MathEvaluationDto} objects.
 * </p>
 */
@Component
public class MatrixEvaluationStrategy implements EvaluationStrategy {

    private final MathCachedEvaluationService mathEvaluator;

    /**
     * Constructs a {@code MatrixEvaluationStrategy} with the given evaluation service.
     *
     * @param mathEvaluator the {@link MathCachedEvaluationService} used to perform evaluations
     */
    public MatrixEvaluationStrategy(MathCachedEvaluationService mathEvaluator) {
        this.mathEvaluator = mathEvaluator;
    }

    /**
     * Computes the evaluation of the matrix expression.
     *
     * @param expression the matrix expression to evaluate
     * @param data       additional data wrapped in {@link MathDataDto}
     * @return a list containing a single {@link MathEvaluationDto} with the evaluation result and any problems
     */
    @Override
    public List<MathEvaluationDto> compute(String expression, MathDataDto data) {
        MathExpressionEvaluation evaluation = mathEvaluator.evaluate(expression);

        formatOperations(mathEvaluator.getFacade(), evaluation);

        mathEvaluator.clearEvaluator();

        return List.of(
                new MathEvaluationDto(MathEvaluationType.EVALUATION,
                        evaluation.getExpressionEvaluated(),
                        evaluation.getEvaluationProblems().orElse(null)
                )
        );
    }

    /**
     * Returns the underlying cached evaluation service instance.
     *
     * @return the {@link MathCachedEvaluationService} instance
     */
    @Override
    public MathCachedEvaluationService getEvaluatorService() {
        return mathEvaluator;
    }
}
