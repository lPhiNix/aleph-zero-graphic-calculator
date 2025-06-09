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
 * {@code VectorEvaluationStrategy} implements {@link EvaluationStrategy} to
 * perform evaluation of vector expressions.
 * <p>
 * This strategy evaluates the vector expression and returns the evaluation result.
 * It does not perform additional calculations or drawings.
 * </p>
 */
@Component
public class VectorEvaluationStrategy implements EvaluationStrategy {

    private final MathCachedEvaluationService mathEvaluator;

    /**
     * Constructs a {@code VectorEvaluationStrategy} with the given evaluator service.
     *
     * @param mathEvaluator the cached evaluation service used to perform vector evaluations
     */
    public VectorEvaluationStrategy(MathCachedEvaluationService mathEvaluator) {
        this.mathEvaluator = mathEvaluator;
    }

    /**
     * Computes the evaluation of the given vector expression.
     *
     * @param expression the vector expression to evaluate
     * @param data       additional input data wrapped in {@link MathDataDto}, not used in this strategy
     * @return a list containing a single {@link MathEvaluationDto} instance for the evaluation result
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
     * Returns the cached evaluation service used by this strategy.
     *
     * @return the {@link MathCachedEvaluationService} instance
     */
    @Override
    public MathCachedEvaluationService getEvaluatorService() {
        return mathEvaluator;
    }
}
