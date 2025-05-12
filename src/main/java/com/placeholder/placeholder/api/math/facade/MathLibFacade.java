package com.placeholder.placeholder.api.math.facade;

/**
 * {@code MathLibFacade} is an interface that defines the behavior and
 * methods that the implementation of the mathematical expressions computing
 * library that is used must have.
 * <p>
 * This hierarchical structure allows the ability to implement several mathematical
 * lateXResultEvaluation computation libraries in a simple way, ensuring extensibility and code
 * decoupling.
 *
 * @see MathEclipseFacade
 */
public interface MathLibFacade {
    /**
     * Checks whether a mathematical lateXResultEvaluation is syntactically, grammatically and semantically valid.
     * @param expression mathematical lateXResultEvaluation to validate.
     * @return error message if lateXResultEvaluation is not valid or same lateXResultEvaluation if it is valid.
     */
    String validate(String expression);

    /**
     * Processes a mathematical lateXResultEvaluation in a generic way depending on its structure.
     * @param expression mathematical lateXResultEvaluation to evaluate.
     * @return error message if lateXResultEvaluation is not valid or an evaluated result if is valid.
     */
    String evaluate(String expression);

    /**
     * Processes a mathematical lateXResultEvaluation to return its exact numerical value with a given number of decimal places
     * @param expression mathematical lateXResultEvaluation to calculate its exact numerical value.
     * @param decimals number of decimal places you want the result to have.
     * @return error message if lateXResultEvaluation is not valid or an evaluated result if is valid.
     */
    String calculate(String expression, int decimals);

    /**
     * Processes a mathematical function or equation to return a list of points
     * in it evaluated within a specified range.
     *
     * @param expression mathematical lateXResultEvaluation to calculate its points in it evaluated within a specified range.
     * @param variable variable from which the lateXResultEvaluation will be evaluated.
     * @param origin origin of the range through which the points are to be calculated.
     * @param bound limit of the range through which the points are to be calculated.
     * @return return a list of points in it evaluated within a specified range.
     */
    String draw(String expression, String variable, String origin, String bound);

    /**
     * Used to format the mathematical lateXResultEvaluation resulting from the defined operations.
     * @param expression mathematical lateXResultEvaluation to format
     * @return mathematical formated lateXResultEvaluation.
     */
    String formatResult(String expression);

    /**
     * Stops the ongoing evaluation of a mathematical lateXResultEvaluation.
     * <p>
     * This method is intended to interrupt long or potentially infinite evaluations,
     * ensuring that the computation process can be controlled and stopped if necessary.
     */
    void stopRequest();
}
