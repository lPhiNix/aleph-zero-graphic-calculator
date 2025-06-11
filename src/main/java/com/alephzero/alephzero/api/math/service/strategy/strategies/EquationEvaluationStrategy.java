package com.alephzero.alephzero.api.math.service.strategy.strategies;

import com.alephzero.alephzero.api.math.dto.request.MathDataDto;
import com.alephzero.alephzero.api.math.dto.response.MathEvaluationDto;
import com.alephzero.alephzero.api.math.enums.computation.MathEvaluationType;
import com.alephzero.alephzero.api.math.facade.MathExpressionEvaluation;
import com.alephzero.alephzero.api.math.service.core.MathCachedEvaluationService;
import com.alephzero.alephzero.api.math.service.strategy.EvaluationStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * {@code EquationEvaluationStrategy} implements the {@link EvaluationStrategy} interface
 * providing the evaluation logic for mathematical equations.
 * <p>
 * This strategy evaluates equations using the underlying {@link MathCachedEvaluationService},
 * returning the result encapsulated in {@link MathEvaluationDto} instances.
 * </p>
 */
@Component
public class EquationEvaluationStrategy implements EvaluationStrategy {

    private final MathCachedEvaluationService mathEvaluator;

    /**
     * Constructs an {@code EquationEvaluationStrategy} with the specified evaluation service.
     *
     * @param mathEvaluator the cached evaluation service to perform computations
     */
    public EquationEvaluationStrategy(MathCachedEvaluationService mathEvaluator) {
        this.mathEvaluator = mathEvaluator;
    }

    /**
     * Computes the evaluation of the provided equation expression.
     *
     * @param expression the equation expression to evaluate
     * @param data       additional context data wrapped in {@link MathDataDto}
     * @return a list with a single {@link MathEvaluationDto} containing the evaluation result and any problems
     */
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

    /**
     * Returns the underlying cached evaluation service used by this strategy.
     *
     * @return the {@link MathCachedEvaluationService} instance
     */
    @Override
    public MathCachedEvaluationService getEvaluatorService() {
        return mathEvaluator;
    }
}
