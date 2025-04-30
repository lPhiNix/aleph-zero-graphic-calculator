package com.placeholder.placeholder.api.facade;

/**
 * {@code MathLibFacade} is an interface that defines the behavior and
 * methods that the implementation of the mathematical expressions computing
 * library that is used must have.
 * <p>
 * This hierarchical structure allows the ability to implement several mathematical
 * expression computation libraries in a simple way, ensuring extensibility and code
 * decoupling.
 *
 * @see MathEclipseFacade
 */
public interface MathLibFacade {
    /**
     * Checks whether a mathematical expression is syntactically, grammatically and semantically valid.
     * @param expression mathematical expression to validate.
     * @return error message if expression is not valid or same expression if it is valid.
     */
    String validate(String expression);

    /**
     * Processes a mathematical expression in a generic way depending on its structure.
     * @param expression mathematical expression to evaluate.
     * @return error message if expression is not valid or an evaluated result if is valid.
     */
    String evaluate(String expression);

    /**
     * Processes a mathematical expression to return its exact numerical value with a given number of decimal places
     * @param expression mathematical expression to calculate its exact numerical value.
     * @param decimals number of decimal places you want the result to have.
     * @return error message if expression is not valid or an evaluated result if is valid.
     */
    String calculate(String expression, int decimals);

    /**
     * Processes a mathematical function or equation to return a list of points
     * in it evaluated within a specified range.
     *
     * @param expression mathematical expression to calculate its points in it evaluated within a specified range.
     * @param variable variable from which the expression will be evaluated.
     * @param origin origin of the range through which the points are to be calculated.
     * @param bound limit of the range through which the points are to be calculated.
     * @return return a list of points in it evaluated within a specified range.
     */
    String draw(String expression, String variable, String origin, String bound);

    /**
     * Used to format the mathematical expression resulting from the defined operations.
     * @param expression mathematical expression to format
     * @return mathematical formated expression.
     */
    String formatResult(String expression);
}
