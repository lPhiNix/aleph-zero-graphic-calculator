package com.placeholder.placeholder.api.math.facade.symja;

import com.placeholder.placeholder.api.math.facade.MathLibFacade;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.form.tex.TeXFormFactory;
import org.matheclipse.core.interfaces.IExpr;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * {@code MathEclipseFacade} is a class that implements the Facade design pattern
 * in the {@code Symja MathEclipse} mathematical expression evaluation and processing library.
 * <p>
 * This class encapsulates and simplifies the library's logic for easy use and implementation.
 * <p>
 * It also implements the expression validator and error handler, using both the
 * library's native validator (syntactic) and its own validator (grammatical and semantic).
 *
 * @see MathLibFacade
 */
@Component
public class MathEclipseFacade implements MathLibFacade<MathEclipseEvaluation> {

    private EvalUtilities mathEclipseEvaluator; // Symja native expression evaluator
    private final TeXFormFactory teXParser; // LaTeX parser

    // Buffer to capture any errors printed to System.err during evaluation
    private final ByteArrayOutputStream errorStream = new ByteArrayOutputStream();

    public MathEclipseFacade(
            EvalUtilities mathEclipseEvaluator,
            TeXFormFactory teXParser
    ) {
        this.mathEclipseEvaluator = mathEclipseEvaluator;
        this.teXParser = teXParser;
    }

    /**
     * Evaluates a validated mathematical expression.
     *
     * @param expression the expression to evaluate
     * @return the result of the evaluation or a formatted error message
     */
    @Override
    public MathEclipseEvaluation evaluate(String expression) {
        // Initial format to clean up the input expression
        String formattedExpression = initialFormatted(expression);
        return safeEvaluation(formattedExpression);
    }

    /**
     * Performs numeric evaluation of an expression with decimal precision.
     *
     * @param expression the input expression
     * @param decimals number of decimal places
     * @return the evaluated numeric result or a formatted error message
     */
    @Override
    public MathEclipseEvaluation calculate(String expression, int decimals) {
        // Prepare the expression for numerical approximation
        String formattedExpression = initialFormatted(expression);
        String numericExpression = N(formattedExpression, decimals);
        return safeEvaluation(numericExpression);
    }

    /**
     * Constructs a Symja plot expression for the given input expression.
     *
     * @param expression function to plot
     * @param variable the independent variable (x-axis)
     * @param origin lower bound of the domain
     * @param bound upper bound of the domain
     * @return the result of the plot expression evaluation, or an error message
     */
    @Override
    public MathEclipseEvaluation draw(String expression, String variable, String origin, String bound) {
        // Format and validate the expression before plotting
        String formattedExpression = initialFormatted(expression);
        String plotExpression = Plot(formattedExpression, variable, origin, bound);
        return safeEvaluation(plotExpression);
    }

    /**
     * Safely evaluates an expression and captures any warnings/errors.
     *
     * @param expression the expression to evaluate
     * @return the result or formatted error message
     */
    private MathEclipseEvaluation safeEvaluation(String expression) {
        // Redirect System.err to capture evaluation warnings or errors
        System.setErr(new PrintStream(errorStream));
        try {
            // Evaluate the expression directly without further validation
            String result = rawEvaluate(expression);

            // Capture any error messages written during evaluation
            String errors = errorStream.toString().trim();

            // Create the evaluation result object
            MathEclipseEvaluation evaluation = new MathEclipseEvaluation(result);
            evaluation.addErrorsFromErrorStream(errors);

            return evaluation;
        } finally {
            // Restore the original System.err to avoid affecting other code
            System.setErr(System.err);
            errorStream.reset();
        }
    }

    /**
     * Performs direct Symja evaluation without validation and error handlers.
     * @param expression expression to validate
     * @return evaluated expression
     */
    private String rawEvaluate(String expression) {
        // Use the internal Symja evaluator to compute the result
        return mathEclipseEvaluator.evaluate(expression).toString();
    }

    /**
     * Formats the result of a mathematical expression into LaTeX format.
     * @param expression the expression to format
     * @return the formatted LaTeX expression
     */
    @Override
    public String formatResult(String expression) {
        return parseToLateX(expression);
    }

    /**
     * Stops any ongoing evaluations in the Symja evaluator.
     */
    @Override
    public void stopRequest() {
        mathEclipseEvaluator.stopRequest();
    }

    /**
     * Resets the internal evaluator to its initial state, removing all variable definitions.
     */
    @Override
    public void clear() {
        // Rebuild the evaluator to clear all stored variables and state
        mathEclipseEvaluator = MathEclipseConfig.buildEvalUtilities();
    }

    /**
     * Applies initial formatting to the expression, including removing spaces and formatting brackets.
     * @param expression the raw expression to format
     * @return the formatted expression
     */
    private String initialFormatted(String expression) {
        expression = removeSpaces(expression);
        expression = formatBranches(expression);
        return expression;
    }

    /**
     * Wraps an expression with Symja's N[] function for numeric approximation.
     *
     * @param expression input expression
     * @param decimals number of decimals
     * @return wrapped expression
     */
    public String N(String expression, int decimals) {
        return "N[" + expression + ", " + decimals + "]";
    }

    /**
     * Builds a Symja-compatible Plot[] expression.
     *
     * @param expression the function to plot
     * @param variable variable of the function
     * @param origin start of the domain
     * @param bound end of the domain
     * @return constructed plot expression
     */
    public String Plot(String expression, String variable, String origin, String bound) {
        return "Plot[" + expression + ", {" + variable + ", " + origin + ", " + bound + "}]";
    }

    /**
     * Converts a Symja expression to LaTeX using {@link TeXFormFactory}.
     *
     * @param wolframExpression the expression in Symja syntax
     * @return LaTeX-formatted expression
     */
    private String parseToLateX(String wolframExpression) {
        IExpr expr = createIExpr(wolframExpression);
        StringBuilder sb = new StringBuilder();
        teXParser.convert(sb, expr);
        return sb.toString();
    }

    /**
     * Parses a string expression into Symja's IExpr representation.
     *
     * @param expression string to parse
     * @return parsed IExpr object
     */
    private IExpr createIExpr(String expression) {
        return new ExprEvaluator().parse(expression);
    }

    /**
     * Utility method to remove all whitespace characters from an expression.
     *
     * @param expression the original expression
     * @return the expression without spaces
     */
    private String removeSpaces(String expression) {
        return expression.replaceAll("\\s+", "");
    }

    /**
     * Formats square brackets to parentheses for consistency in Symja.
     *
     * @param expression the original expression
     * @return the expression with standardized brackets
     */
    private String formatBranches(String expression) {
        if (expression == null || expression.isBlank()) {
            return expression;
        }
        return expression.replace("[", "(").replace("]", ")");
    }
}