package com.alephzero.alephzero.api.math.facade;

import java.util.List;
import java.util.Optional;

/**
 * {@code MathExpressionEvaluation} interface represents the result of evaluating a mathematical expression
 * inside native lib facade.
 * <p>
 * This interface provides methods to retrieve the evaluated form of the expression
 * and any potential issues encountered during the evaluation process.
 */
public interface MathExpressionEvaluation {
    /**
     * Returns the evaluated form of the mathematical expression.
     * <p>
     * This result is typically a simplified, formatted, or computed representation of the original expression.
     *
     * @return the evaluated expression as a string.
     */
    String getExpressionEvaluated();

    String format(String newExpression);

    /**
     * Returns an optional list of evaluation problems encountered during the processing of the expression.
     * <p>
     * This is useful for capturing warnings, errors, or any other issues that may have occurred
     * while attempting to evaluate the expression.
     * <p>
     * NOTE: This implementation is optional in case the library you are implementing does not
     * contain or support a problem-providing system when computing expressions.
     *
     * @return an {@link Optional} containing a list of problem descriptions if present, otherwise an empty {@link Optional}.
     */
    default Optional<List<String>> getEvaluationProblems() {
        return Optional.empty();
    }
}
