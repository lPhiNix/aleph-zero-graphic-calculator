package com.alephzero.alephzero.api.math.service.strategy;

import com.alephzero.alephzero.api.math.dto.request.MathDataDto;
import com.alephzero.alephzero.api.math.dto.response.MathEvaluationDto;
import com.alephzero.alephzero.api.math.facade.MathExpressionEvaluation;
import com.alephzero.alephzero.api.math.facade.MathLibFacade;
import com.alephzero.alephzero.api.math.service.core.MathCachedEvaluationService;

import java.util.List;

/**
 * {@code EvaluationStrategy} defines the contract for evaluating mathematical expressions
 * according to different expression types or evaluation approaches.
 * <p>
 * Implementations provide the logic to compute the evaluation results and return
 * a list of {@link MathEvaluationDto} representing the evaluation output.
 * <p>
 * This interface also provides access to the underlying {@link MathCachedEvaluationService}
 * used to perform the actual computation and caching.
 */
public interface EvaluationStrategy {

    /**
     * Computes the evaluation of the given mathematical expression with the provided context data.
     *
     * @param expression the mathematical expression to evaluate as a {@link String}
     * @param data       additional data required for evaluation wrapped in {@link MathDataDto}
     * @return a list of {@link MathEvaluationDto} containing the results of the evaluation
     */
    List<MathEvaluationDto> compute(String expression, MathDataDto data);

    /**
     * Returns the underlying evaluation service used by this strategy.
     *
     * @return the {@link MathCachedEvaluationService} instance used for computation and caching
     */
    MathCachedEvaluationService getEvaluatorService();

    default void formatOperations(MathLibFacade mathLibFacade, MathExpressionEvaluation... evaluations) {
        for (MathExpressionEvaluation evaluation : evaluations) {
            evaluation.format(mathLibFacade.formatResult(evaluation.getExpressionEvaluated()));
        }
    }

    /**
     * Requests to stop any ongoing evaluation if supported by the underlying evaluation service.
     * <p>
     * This default method invokes the {@code stopRequest()} method on the evaluator service
     * if it is present, allowing cancellation of long-running computations.
     */
    default void stopRequestIfSupported() {
        MathCachedEvaluationService evaluatorService = getEvaluatorService();
        if (evaluatorService != null) {
            evaluatorService.stopRequest();
        }
    }
}
