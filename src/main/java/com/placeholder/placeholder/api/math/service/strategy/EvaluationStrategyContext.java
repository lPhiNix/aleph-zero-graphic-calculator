package com.placeholder.placeholder.api.math.service.strategy;

import com.placeholder.placeholder.api.math.enums.computation.MathExpressionType;
import com.placeholder.placeholder.api.math.service.strategy.strategies.*;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * {@code EvaluationStrategyContext} acts as the context class in the Strategy design pattern.
 * <p>
 * It maps each {@link MathExpressionType} to its corresponding {@link EvaluationStrategy} implementation
 * and provides the appropriate strategy to be used for evaluating a specific type of mathematical expression.
 * <p>
 * All strategies are injected via Spring and registered automatically based on their type.
 */
@Component
public class EvaluationStrategyContext {

    /**
     * Internal map binding each {@link MathExpressionType} to its corresponding {@link EvaluationStrategy}.
     */
    private final Map<MathExpressionType, EvaluationStrategy> evaluationStrategyMap = new EnumMap<>(MathExpressionType.class);

    /**
     * Initializes the strategy context with a list of available strategies.
     * <p>
     * Each strategy is matched to a {@link MathExpressionType} and stored in the internal map.
     *
     * @param strategies list of all available {@link EvaluationStrategy} implementations
     */
    public EvaluationStrategyContext(
            List<EvaluationStrategy> strategies
    ) {
        for (EvaluationStrategy strategy : strategies) {
            evaluationStrategyMap.put(getSupportedType(strategy), strategy);
        }
    }

    /**
     * Retrieves the appropriate strategy based on the provided expression type.
     * <p>
     * If no specific strategy is found for the type, the {@link MathExpressionType#UNKNOWN} strategy is returned.
     *
     * @param type the type of expression to evaluate
     * @return the corresponding {@link EvaluationStrategy}
     */
    public EvaluationStrategy getStrategy(MathExpressionType type) {
        return evaluationStrategyMap.getOrDefault(type, evaluationStrategyMap.get(MathExpressionType.UNKNOWN));
    }

    /**
     * Determines the {@link MathExpressionType} that a specific strategy supports.
     * <p>
     * Used internally during initialization to map strategies correctly.
     *
     * @param strategy the evaluation strategy instance
     * @return the supported expression type
     * @throws IllegalStateException if the strategy type is not recognized
     */
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

    /**
     * Stops all currently running evaluations for each strategy.
     * <p>
     * This method is useful for canceling evaluations on shutdown or when a user interrupts a request.
     */
    public void stopCurrentEvaluation() {
        evaluationStrategyMap.values().forEach(EvaluationStrategy::stopRequestIfSupported);
    }
}
