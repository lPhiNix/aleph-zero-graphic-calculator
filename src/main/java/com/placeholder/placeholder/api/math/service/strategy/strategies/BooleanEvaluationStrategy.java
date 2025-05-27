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
 * {@code BooleanEvaluationStrategy} implements the {@link EvaluationStrategy} interface
 * to provide evaluation logic specifically for boolean expressions.
 * <p>
 * This strategy uses the underlying {@link MathCachedEvaluationService} to compute the evaluation
 * of boolean mathematical expressions and returns the results wrapped in
 * {@link MathEvaluationDto} objects.
 * </p>
 * <p>
 * It focuses on evaluating expressions that return boolean results (true/false),
 * handling any evaluation problems internally.
 * </p>
 */
@Component
public class BooleanEvaluationStrategy implements EvaluationStrategy {

    private final MathCachedEvaluationService mathEvaluator;

    /**
     * Constructs a {@code BooleanEvaluationStrategy} with the specified cached evaluation service.
     *
     * @param mathEvaluator the {@link MathCachedEvaluationService} to perform evaluations
     */
    public BooleanEvaluationStrategy(MathCachedEvaluationService mathEvaluator) {
        this.mathEvaluator = mathEvaluator;
    }

    /**
     * Computes the evaluation of the given boolean expression using the cached evaluator service.
     *
     * @param expression the mathematical boolean expression to evaluate
     * @param data       additional data for evaluation wrapped in {@link MathDataDto}
     * @return a list containing a single {@link MathEvaluationDto} with the evaluation result and any problems
     */
    @Override
    public List<MathEvaluationDto> compute(String expression, MathDataDto data) {
        MathExpressionEvaluation evaluation = mathEvaluator.evaluate(expression);

        formatOperations(mathEvaluator.getFacade(), evaluation);

        return List.of(
                new MathEvaluationDto(MathEvaluationType.EVALUATION,
                        evaluation.getExpressionEvaluated(),
                        evaluation.getEvaluationProblems().orElse(null)
                )
        );
    }

    /**
     * Returns the underlying {@link MathCachedEvaluationService} used for boolean evaluations.
     *
     * @return the cached evaluation service instance
     */
    @Override
    public MathCachedEvaluationService getEvaluatorService() {
        return mathEvaluator;
    }
}
