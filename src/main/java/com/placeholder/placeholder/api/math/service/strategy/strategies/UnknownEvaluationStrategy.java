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
 * {@code UnknownEvaluationStrategy} implements {@link EvaluationStrategy} to
 * handle expressions of unknown or generic types, providing evaluation and drawing.
 * <p>
 * This strategy evaluates the expression and generates a graphical drawing
 * based on the evaluation and input data.
 * </p>
 */
@Component
public class UnknownEvaluationStrategy implements EvaluationStrategy {

    private final MathCachedEvaluationService mathEvaluator;

    /**
     * Constructs an {@code UnknownEvaluationStrategy} with the provided evaluator service.
     *
     * @param mathEvaluator the cached evaluation service used for evaluating and drawing expressions
     */
    public UnknownEvaluationStrategy(MathCachedEvaluationService mathEvaluator) {
        this.mathEvaluator = mathEvaluator;
    }

    /**
     * Computes the evaluation and drawing of the given expression using input data.
     *
     * @param expression the mathematical expression to be evaluated and drawn
     * @param data       additional data wrapped in {@link MathDataDto} used for drawing parameters
     * @return a list containing two {@link MathEvaluationDto} instances: one for evaluation and one for drawing
     */
    @Override
    public List<MathEvaluationDto> compute(String expression, MathDataDto data) {
        MathExpressionEvaluation evaluation = mathEvaluator.evaluate(expression);
        MathExpressionEvaluation draw = mathEvaluator.draw(expression, data);

        return List.of(
                new MathEvaluationDto(MathEvaluationType.EVALUATION,
                        evaluation.getExpressionEvaluated(),
                        evaluation.getEvaluationProblems().orElse(null)
                ),
                new MathEvaluationDto(MathEvaluationType.DRAWING,
                        draw.getExpressionEvaluated(),
                        draw.getEvaluationProblems().orElse(null)
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
