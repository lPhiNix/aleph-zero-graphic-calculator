package com.placeholder.placeholder.api.math.service.strategy;

import com.placeholder.placeholder.api.math.enums.computation.MathExpressionType;
import com.placeholder.placeholder.api.math.service.strategy.strategies.*;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class EvaluationStrategyContext {
    private final Map<MathExpressionType, EvaluationStrategy> evaluationStrategyMap = new EnumMap<>(MathExpressionType.class);

    public EvaluationStrategyContext(
            List<EvaluationStrategy> strategies
    ) {
        for (EvaluationStrategy strategy : strategies) {
            evaluationStrategyMap.put(getSupportedType(strategy), strategy);
        }
    }

    public EvaluationStrategy getStrategy(MathExpressionType type) {
        return evaluationStrategyMap.getOrDefault(type, evaluationStrategyMap.get(MathExpressionType.UNKNOWN));
    }

    private MathExpressionType getSupportedType(EvaluationStrategy strategy) {
        return switch (strategy) {
            case FunctionEvaluationStrategy s -> MathExpressionType.FUNCTION;
            case EquationEvaluationStrategy s -> MathExpressionType.EQUATION;
            case BooleanEvaluationStrategy s -> MathExpressionType.BOOLEAN;
            case NumericEvaluationStrategy s -> MathExpressionType.NUMERIC;
            case VectorEvaluationStrategy s -> MathExpressionType.VECTOR;
            case MatrixEvaluationStrategy s -> MathExpressionType.MATRIX;
            case AssignmentEvaluationStrategy s -> MathExpressionType.ASSIGNMENT;
            case UnknownEvaluationStrategy s -> MathExpressionType.UNKNOWN;
            default -> throw new IllegalStateException("Unsupported strategy: " + strategy.getClass().getSimpleName());
        };
    }

    public void stopCurrentEvaluation() {
        evaluationStrategyMap.values().forEach(EvaluationStrategy::stopRequestIfSupported);
    }
}
