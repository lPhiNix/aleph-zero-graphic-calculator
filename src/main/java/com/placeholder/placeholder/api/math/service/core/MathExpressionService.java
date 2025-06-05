package com.placeholder.placeholder.api.math.service.core;

import com.placeholder.placeholder.api.math.dto.request.MathDataDto;
import com.placeholder.placeholder.api.math.dto.request.MathEvaluationRequest;
import com.placeholder.placeholder.api.math.dto.response.MathEvaluationDto;
import com.placeholder.placeholder.api.math.dto.response.MathEvaluationResultResponse;
import com.placeholder.placeholder.api.math.dto.response.MathExpressionEvaluationDto;
import com.placeholder.placeholder.api.math.enums.computation.MathExpressionType;
import com.placeholder.placeholder.api.math.exception.MathEvaluationTimeoutException;
import com.placeholder.placeholder.api.math.service.classifier.Classifier;
import com.placeholder.placeholder.api.math.service.memory.MathAssignmentMemory;
import com.placeholder.placeholder.api.math.service.strategy.EvaluationStrategyContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;

/**
 * {@code MathExpressionService} implements the main service for evaluating a list of mathematical expressions.
 * <p>
 * It applies a timeout to prevent long evaluations and uses a strategy pattern to dispatch evaluation logic
 * according to the expression type.
 * <p>
 * The service also stores intermediate results in memory and clears them after each evaluation cycle.
 */
@Service
public class MathExpressionService implements MathEvaluationService {
    private final ExecutorService executor;

    /** Maximum allowed time (in seconds) for expression evaluation */
    private static final long TIMEOUT_SECONDS = 60;

    private final MathAssignmentMemory memory;
    private final Classifier mathExpressionClassifier;
    private final EvaluationStrategyContext context;

    /**
     * Constructs a new {@code MathExpressionService} with the required dependencies.
     *
     * @param memory the memory used for storing assignments and definitions
     * @param mathExpressionClassifier the classifier for determining expression type
     * @param context the strategy context to delegate expression evaluation
     */
    @Autowired
    public MathExpressionService(
            @Qualifier("mathThreadPool") ExecutorService executor,
            MathAssignmentMemory memory,
            Classifier mathExpressionClassifier,
            EvaluationStrategyContext context
    ) {
        this.executor = executor;
        this.memory = memory;
        this.mathExpressionClassifier = mathExpressionClassifier;
        this.context = context;
    }

    /**
     * Evaluates a batch of mathematical expressions provided in the request.
     * It applies a timeout and handles cancellation and errors gracefully.
     *
     * @param request the request containing expressions and input data
     * @return a result wrapper with all evaluations
     */
    @Override
    public MathEvaluationResultResponse evaluation(MathEvaluationRequest request) {
        return evaluateWithTimeout(request);
    }

    /**
     * Wraps the evaluation process with a timeout and task cancellation support.
     * If the evaluation exceeds {@code TIMEOUT_SECONDS}, it will throw a timeout exception.
     *
     * @param request the evaluation request
     * @return the final evaluation response with all expression results
     */
    private MathEvaluationResultResponse evaluateWithTimeout(MathEvaluationRequest request) {
        try {
            Future<MathEvaluationResultResponse> future = executor.submit(() -> evaluateExpressions(request));
            return future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            context.stopCurrentEvaluation();
            throw new MathEvaluationTimeoutException("Timeout after " + TIMEOUT_SECONDS + " seconds");
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error evaluating math expressions", e);
        }
    }

    /**
     * Iterates over each input expression, processes it, and evaluates it using
     * the strategy corresponding to its classified type.
     *
     * @param request the request containing multiple expressions and input data
     * @return the aggregated evaluation response
     */
    private MathEvaluationResultResponse evaluateExpressions(MathEvaluationRequest request) {
      // create a list of futures for each expression evaluation
        List<CompletableFuture<MathExpressionEvaluationDto>> futures = request.expressions().stream()
                .map(expr -> CompletableFuture.supplyAsync(
                        () -> evaluateSingleExpression(expr.expression(), request.data()),
                        executor
                ))
                .toList();

        // Wait for all futures to complete and collect results
        List<MathExpressionEvaluationDto> evaluations = futures.stream()
                .map(CompletableFuture::join) // join blocks until the future completes
                .toList();

        // clear memory after evaluation
        memory.clear();

        return new MathEvaluationResultResponse(evaluations);
    }


    /**
     * Evaluates a single expression. It first processes and classifies the expression,
     * then selects and executes the appropriate evaluation strategy.
     *
     * @param rawExpression the raw mathematical expression as a string
     * @param data the input data context used in evaluation
     * @return the result of the evaluation including the original expression, its type, and outputs
     */
    private MathExpressionEvaluationDto evaluateSingleExpression(String rawExpression, MathDataDto data) {
        // Apply memory processing (e.g., variable substitution or definitions)
        String processed = memory.process(rawExpression);

        // Determine the type of expression (e.g., assignment, equation, function)
        MathExpressionType type = mathExpressionClassifier.classify(processed);

        // Compute the result using the selected evaluation strategy
        List<MathEvaluationDto> results = context.getStrategy(type).compute(processed, data);

        return new MathExpressionEvaluationDto(rawExpression, type, results);
    }
}
