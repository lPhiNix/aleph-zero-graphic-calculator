package com.placeholder.placeholder.api.math.service.strategy.strategies;

import com.placeholder.placeholder.api.math.dto.request.MathDataDto;
import com.placeholder.placeholder.api.math.dto.response.MathEvaluationDto;
import com.placeholder.placeholder.api.math.service.core.MathCachedEvaluationService;
import com.placeholder.placeholder.api.math.service.strategy.EvaluationStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * {@code AssignmentEvaluationStrategy} implements the {@link EvaluationStrategy} interface
 * and defines the evaluation logic for assignment-type mathematical expressions.
 * <p>
 * This strategy is responsible for computing the evaluation results of expressions
 * involving assignments and returning the results in a list of {@link MathEvaluationDto}.
 * <p>
 * Currently, this implementation returns an empty list and does not provide
 * an underlying evaluation service.
 *
 * @see EvaluationStrategy
 * @see MathEvaluationDto
 */
@Component
public class AssignmentEvaluationStrategy implements EvaluationStrategy {

    /**
     * Computes the evaluation results of the given assignment expression
     * using the provided contextual data.
     *
     * @param expression the mathematical assignment expression to evaluate as a {@link String}
     * @param data       additional data required for evaluation wrapped in {@link MathDataDto}
     * @return a list of {@link MathEvaluationDto} containing the evaluation results;
     *         currently returns an empty list
     */
    @Override
    public List<MathEvaluationDto> compute(String expression, MathDataDto data) {
        return List.of();
    }

    /**
     * Returns the underlying evaluation service used by this strategy.
     *
     * @return {@code null} as no evaluation service is currently assigned
     */
    @Override
    public MathCachedEvaluationService getEvaluatorService() {
        return null;
    }
}
