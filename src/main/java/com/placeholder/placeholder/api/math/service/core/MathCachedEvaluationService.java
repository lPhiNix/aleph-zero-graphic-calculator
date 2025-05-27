package com.placeholder.placeholder.api.math.service.core;

import com.placeholder.placeholder.api.math.dto.request.MathDataDto;
import com.placeholder.placeholder.api.math.facade.MathExpressionEvaluation;
import com.placeholder.placeholder.api.math.facade.MathLibFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * {@code MathCachedEvaluationService} is a service class responsible for evaluating, calculating
 * and graphing mathematical expressions using a cached layer to improve performance.
 * <p>
 * It acts as a wrapper around the {@link MathLibFacade} and delegates evaluation logic
 * while applying Spring's caching mechanism to avoid redundant calculations.
 * <p>
 * It also provides methods to reset or stop the internal evaluator.
 *
 * @see MathLibFacade
 */
@Service
public class MathCachedEvaluationService {

    private final MathLibFacade mathEclipse;

    /**
     * Constructs a cached evaluation service with the given math facade implementation.
     *
     * @param mathEclipse the math library facade used for evaluation
     */
    @Autowired
    public MathCachedEvaluationService(MathLibFacade mathEclipse) {
        this.mathEclipse = mathEclipse;
    }

    /**
     * Evaluates a symbolic mathematical expression using the facade and caches the result.
     *
     * @param expression the expression to evaluate
     * @return the symbolic evaluation result
     */
    @Cacheable(value = "evaluate", key = "#expression")
    public MathExpressionEvaluation evaluate(String expression) {
        return mathEclipse.evaluate(expression);
    }

    /**
     * Performs numerical evaluation of a mathematical expression with a specified decimal precision,
     * and caches the result for the given expression and precision.
     *
     * @param expression the expression to evaluate
     * @param data contains the number of decimals to use
     * @return the numeric evaluation result
     */
    @Cacheable(value = "calculate", key = "#expression + '_' + #data.decimals()")
    public MathExpressionEvaluation calculate(String expression, MathDataDto data) {
        return mathEclipse.calculate(expression, data.decimals());
    }

    /**
     * Generates a plot-ready evaluation of the given expression across a domain defined by the data bounds,
     * and caches the result.
     *
     * @param expression the function expression to plot
     * @param data contains the origin and bound of the domain
     * @return the plot expression evaluation result
     */
    @Cacheable(value = "draw", key = "#expression + '_' + #data.origin() + '_' + #data.bound()")
    public MathExpressionEvaluation draw(String expression, MathDataDto data) {
        return mathEclipse.draw(expression, "x", data.origin(), data.bound());
    }

    /**
     * Clears the state of the underlying evaluator by removing all stored variables and definitions.
     */
    public void clearEvaluator() {
        mathEclipse.clear();
    }

    /**
     * Stops any ongoing or long-running evaluation request in the underlying math evaluator.
     */
    public void stopRequest() {
        mathEclipse.stopRequest();
    }

    public MathLibFacade getFacade() {
        return mathEclipse;
    }
}
