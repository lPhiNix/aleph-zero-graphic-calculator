package com.alephzero.alephzero.api.math.service.core;

import com.alephzero.alephzero.api.math.dto.request.MathDataDto;
import com.alephzero.alephzero.api.math.facade.MathExpressionEvaluation;
import com.alephzero.alephzero.api.math.facade.MathLibFacade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.Semaphore;

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
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MathCachedEvaluationService {

    private static final Logger logger = LogManager.getLogger(MathCachedEvaluationService.class);

    /**
     * Thread-local holder for MathLibFacade.
     */
    private final ThreadLocal<MathLibFacade> threadLocalFacade;

    /**
     * Semaphore to limit concurrent Symja evaluations.
     */
    private final Semaphore semaphore = new Semaphore(20);

    /**
     * Constructs a cached evaluation service with a provider for math facade instances.
     *
     * @param facadeProvider the Spring provider for prototype MathLibFacade beans
     */
    @Autowired
    public MathCachedEvaluationService(ObjectProvider<MathLibFacade> facadeProvider) {
        // Provider for prototype-scoped MathLibFacade instances.
        this.threadLocalFacade = ThreadLocal.withInitial(facadeProvider::getObject);
        logger.info("MathCachedEvaluationService initialized with prototype MathLibFacade provider and semaphore");
    }

    /**
     * Retrieves the thread-local MathLibFacade instance.
     */
    private MathLibFacade getFacade() {
        return threadLocalFacade.get();
    }

    /**
     * Evaluates a symbolic mathematical expression using the facade and caches the result.
     *
     * @param expression the expression to evaluate
     * @return the symbolic evaluation result
     */
    @Cacheable(value = "evaluate", key = "#expression")
    public MathExpressionEvaluation evaluate(String expression) {
        try {
            semaphore.acquire();
            logger.info("evaluate() acquired semaphore, permits left={}", semaphore.availablePermits());
            MathExpressionEvaluation result = getFacade().evaluate(expression);
            getFacade().clear();
            return result;
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for Symja permit", ie);
        } finally {
            semaphore.release();
            logger.debug("evaluate() released semaphore, permits left={}", semaphore.availablePermits());
        }
    }

    /**
     * Performs numerical evaluation of a mathematical expression with a specified decimal precision,
     * and caches the result for the given expression and precision.
     *
     * @param expression the expression to evaluate
     * @param data       contains the number of decimals to use
     * @return the numeric evaluation result
     */
    @Cacheable(value = "calculate", key = "#expression + '_' + #data.decimals()")
    public MathExpressionEvaluation calculate(String expression, MathDataDto data) {
        try {
            semaphore.acquire();
            logger.info("calculate() acquired semaphore, permits left={}", semaphore.availablePermits());
            MathExpressionEvaluation result = getFacade().calculate(expression, data.decimals());
            getFacade().clear();
            return result;
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for Symja permit", ie);
        } finally {
            semaphore.release();
            logger.debug("calculate() released semaphore, permits left={}", semaphore.availablePermits());
        }
    }

    /**
     * Generates a plot-ready evaluation of the given expression across a domain defined by the data bounds,
     * and caches the result.
     *
     * @param expression the function expression to plot
     * @param data       contains the origin and bound of the domain
     * @return the plot expression evaluation result
     */
    @Cacheable(value = "draw", key = "#expression + '_' + #data.origin() + '_' + #data.bound()")
    public MathExpressionEvaluation draw(String expression, MathDataDto data) {
        try {
            semaphore.acquire();
            logger.info("draw() acquired semaphore, permits left={}", semaphore.availablePermits());
            // Pre-evaluate expression
            expression = evaluate(expression).getExpressionEvaluated();
            MathExpressionEvaluation result = getFacade().draw(expression, "x", data.origin(), data.bound());
            getFacade().clear();
            return result;
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for Symja permit", ie);
        } finally {
            semaphore.release();
            logger.debug("draw() released semaphore, permits left={}", semaphore.availablePermits());
        }
    }

    /**
     * Stops any ongoing or long-running evaluation request in the underlying math evaluator.
     */
    public void stopRequest() {
        logger.info("stopRequest() called - stopping ongoing evaluation");
        getFacade().stopRequest();
        logger.debug("Ongoing evaluation stopped");
    }

    /**
     * Provides direct access to the underlying MathLibFacade for advanced operations.
     */
    public MathLibFacade getFacadeInstance() {
        return getFacade();
    }
}
