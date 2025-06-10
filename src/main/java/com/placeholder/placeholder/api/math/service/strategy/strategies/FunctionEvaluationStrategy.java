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
 * {@code FunctionEvaluationStrategy} implements {@link EvaluationStrategy} to
 * provide evaluation logic for mathematical functions.
 * <p>
 * This strategy evaluates the function expression and additionally requests
 * a drawing (plot) evaluation using the given input data.
 * The results are returned as a list of {@link MathEvaluationDto} containing
 * both evaluation and drawing results.
 * </p>
 */
@Component
public class FunctionEvaluationStrategy implements EvaluationStrategy {

    private final MathCachedEvaluationService mathEvaluator;

    /**
     * Constructs a {@code FunctionEvaluationStrategy} with the provided evaluator service.
     *
     * @param mathEvaluator the cached evaluation service used for computations and drawings
     */
    public FunctionEvaluationStrategy(MathCachedEvaluationService mathEvaluator) {
        this.mathEvaluator = mathEvaluator;
    }

    /**
     * Computes both the function evaluation and its graphical drawing based on the input data.
     *
     * @param expression the function expression to evaluate
     * @param data       additional data wrapped in {@link MathDataDto}, used for drawing parameters
     * @return a list of two {@link MathEvaluationDto} instances: one for evaluation and one for drawing
     */
    @Override
    public List<MathEvaluationDto> compute(String expression, MathDataDto data) {
        MathExpressionEvaluation evaluation = mathEvaluator.evaluate(expression);
        MathExpressionEvaluation draw = mathEvaluator.draw(evaluation.getExpressionEvaluated(), data);

        formatOperations(mathEvaluator.getFacade(), evaluation);

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
     * Returns the underlying cached evaluation service.
     *
     * @return the {@link MathCachedEvaluationService} instance
     */
    @Override
    public MathCachedEvaluationService getEvaluatorService() {
        return mathEvaluator;
    }
}
