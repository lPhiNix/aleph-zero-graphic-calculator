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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;

@Service
public class MathExpressionService implements MathEvaluationService {

    private static final long TIMEOUT_SECONDS = 3 * 60;

    private final MathAssignmentMemory memory;
    private final Classifier mathExpressionClassifier;
    private final EvaluationStrategyContext context;

    @Autowired
    public MathExpressionService(
            MathAssignmentMemory memory,
            Classifier mathExpressionClassifier,
            EvaluationStrategyContext context
    ) {
        this.memory = memory;
        this.mathExpressionClassifier = mathExpressionClassifier;
        this.context = context;
    }

    @Override
    public MathEvaluationResultResponse evaluation(MathEvaluationRequest request) {
        return evaluateWithTimeout(request);
    }

    private MathEvaluationResultResponse evaluateWithTimeout(MathEvaluationRequest request) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            Future<MathEvaluationResultResponse> future = executor.submit(() -> evaluateExpressions(request));
            return future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            executor.shutdownNow();
            context.stopCurrentEvaluation();
            throw new MathEvaluationTimeoutException("La evaluación excedió el tiempo máximo de espera (" + TIMEOUT_SECONDS + "s)");
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error al evaluar la expresión matemática", e);
        } finally {
            executor.shutdown();
        }
    }

    private MathEvaluationResultResponse evaluateExpressions(MathEvaluationRequest request) {
        List<MathExpressionEvaluationDto> evaluations = request.expressions().stream()
                .map(expr -> evaluateSingleExpression(expr.expression(), request.data()))
                .toList();

        memory.clear();

        return new MathEvaluationResultResponse(evaluations);
    }

    private MathExpressionEvaluationDto evaluateSingleExpression(String rawExpression, MathDataDto data) {
        String processed = memory.process(rawExpression);
        MathExpressionType type = mathExpressionClassifier.classify(processed);
        List<MathEvaluationDto> results = context.getStrategy(type).compute(processed, data);
        return new MathExpressionEvaluationDto(rawExpression, type, results);
    }
}
