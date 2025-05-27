package com.placeholder.placeholder.api.math.facade;

/**
 * {@code MathLibFacade} defines the core interface for mathematical expression evaluation,
 * providing methods for evaluating, calculating, drawing, formatting, and validating expressions.
 * <p>
 * This structure allows flexibility in integrating different mathematical libraries, promoting
 * code decoupling and extensibility.
 */
public interface MathLibFacade<E extends MathExpressionEvaluation> {
    /**
     * Evaluates a mathematical expression, returning the result as an instance of {@link MathExpressionEvaluation}.
     *
     * @param expression the mathematical expression to evaluate.
     * @return the evaluated result.
     */
    E evaluate(String expression);

    /**
     * Computes the exact numerical value of a mathematical expression, formatted to the specified number of decimal places.
     *
     * @param expression the mathematical expression to calculate.
     * @param decimals the number of decimal places to include in the result.
     * @return the calculated result.
     */
    E calculate(String expression, int decimals);

    /**
     * Generates a series of evaluated points from a mathematical function or equation over a specified range.
     *
     * @param expression the mathematical expression to evaluate.
     * @param variable the variable with respect to which the expression is evaluated.
     * @param origin the starting point of the evaluation range.
     * @param bound the ending point of the evaluation range.
     * @return a list of evaluated points within the specified range.
     */
    E draw(String expression, String variable, String origin, String bound);

    /**
     * Formats a mathematical expression result, ensuring consistent output presentation.
     *
     * @param expression the mathematical expression to format.
     * @return the formatted mathematical expression as a string.
     */
    String formatResult(String expression);

    /**
     * Stops the ongoing evaluation of a mathematical expression, if supported by the underlying library.
     * <p>
     * This method is useful for interrupting long or potentially infinite evaluations,
     * allowing for controlled cancellation.
     */
    void stopRequest();

    /**
     * Cleans the internal state of the mathematical evaluator, resetting all variable assignments and cached values.
     * <p>
     * This method is typically used to clear the internal context after a series of evaluations,
     * ensuring a fresh evaluation state for subsequent computations.
     */
    void clear();
}