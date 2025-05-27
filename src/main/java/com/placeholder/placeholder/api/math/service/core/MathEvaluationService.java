package com.placeholder.placeholder.api.math.service.core;

import com.placeholder.placeholder.api.math.dto.request.MathEvaluationRequest;
import com.placeholder.placeholder.api.math.dto.response.MathEvaluationResultResponse;

/**
 * {@code MathEvaluationService} is an interface that defines the contract for evaluating
 * mathematical expressions within the application.
 * <p>
 * It acts as the service layer entry point that receives the request containing the
 * input expression and any additional evaluation parameters, and returns the evaluated result
 * wrapped in a structured response.
 * <p>
 * Implementations of this interface are responsible for processing, validating, and delegating
 * the evaluation logic to the appropriate library or facade.
 */
public interface MathEvaluationService {

    /**
     * Evaluates a mathematical expression based on the provided request parameters.
     *
     * @param request the input evaluation request containing the expression and metadata
     * @return the result of the evaluation, including expression output and any errors
     */
    MathEvaluationResultResponse evaluation(MathEvaluationRequest request);
}
