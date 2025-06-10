package com.placeholder.placeholder.api.math.facade.symja;

import com.placeholder.placeholder.api.math.facade.MathLibFacade;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.exception.TimeoutException;
import org.matheclipse.core.form.tex.TeXFormFactory;
import org.matheclipse.core.interfaces.IExpr;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
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

    private static final Logger logger = LogManager.getLogger(MathEclipseFacade.class);

    private EvalUtilities mathEclipseEvaluator; // Symja native expression evaluator
    private final TeXFormFactory teXParser;     // LaTeX parser

    @Autowired
    public MathEclipseFacade(
            EvalUtilities mathEclipseEvaluator,
            TeXFormFactory teXParser
    ) {
        this.mathEclipseEvaluator = mathEclipseEvaluator;
        this.teXParser = teXParser;
        logger.info("MathEclipseFacade initialized with EvalUtilities and TeXFormFactory");
    }

    /**
     * Evaluates a validated mathematical expression.
     *
     * @param expression the expression to evaluate
     * @return the result of the evaluation or a formatted error message
     */
    @Override
    public MathEclipseEvaluation evaluate(String expression) {
        logger.info("Entering evaluate() with raw expression: {}", expression);
        String formattedExpression = initialFormatted(expression);
        logger.debug("Formatted expression for evaluate(): {}", formattedExpression);
        MathEclipseEvaluation evaluation = safeEvaluation(formattedExpression);
        logger.info("Exiting evaluate() with result: {}", evaluation.getExpressionEvaluated());
        return evaluation;
    }

    /**
     * Performs numeric evaluation of an expression with decimal precision.
     *
     * @param expression the input expression
     * @param decimals   number of decimal places
     * @return the evaluated numeric result or a formatted error message
     */
    @Override
    public MathEclipseEvaluation calculate(String expression, int decimals) {
        logger.info("Entering calculate() with raw expression: {} and decimals: {}", expression, decimals);
        String formattedExpression = initialFormatted(expression);
        logger.debug("Formatted expression for calculate(): {}", formattedExpression);
        String numericExpression = N(formattedExpression, decimals);
        logger.debug("Numeric expression for calculate(): {}", numericExpression);
        MathEclipseEvaluation evaluation = safeEvaluation(numericExpression);
        logger.info("Exiting calculate() with result: {}", evaluation.getExpressionEvaluated());
        return evaluation;
    }

    /**
     * Constructs a Symja plot expression for the given input expression.
     *
     * @param expression function to plot
     * @param variable   the independent variable (x-axis)
     * @param origin     lower bound of the domain
     * @param bound      upper bound of the domain
     * @return the result of the plot expression evaluation, or an error message
     */
    @Override
    public MathEclipseEvaluation draw(String expression, String variable, String origin, String bound) {
        logger.debug("Entering draw() with expression: {}, variable: {}, origin: {}, bound: {}", expression, variable, origin, bound);
        String formattedExpression = initialFormatted(expression);
        logger.debug("Formatted expression for plotting: {}", formattedExpression);

        String plotExpression = Plot(formattedExpression, variable, origin, bound);
        logger.debug("Plot expression constructed: {}", plotExpression);

        MathEclipseEvaluation evaluation = safeEvaluation(plotExpression);
        logger.debug("Plot evaluation result: {}", evaluation);
        return evaluation;
    }

    /**
     * Safely evaluates an expression and captures any warnings/errors.
     *
     * @param expression the expression to evaluate
     * @return the result or formatted error message
     */
    private MathEclipseEvaluation safeEvaluation(String expression) {
        logger.debug("Entering safeEvaluation() with expression: {}", expression);
        // Redirect System.err to capture evaluation warnings or errors
        PrintStream originalErr = System.err;

        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errorStream));
        try {
            String result = rawEvaluate(expression);
            logger.debug("Raw evaluation returned: {}", result);

            // Capture any error messages written during evaluation
            String errors = errorStream.toString().trim();
            if (!errors.isEmpty()) {
                logger.warn("Errors captured during evaluation: {}", errors);
            }

            // Create the evaluation result object
            MathEclipseEvaluation evaluation = new MathEclipseEvaluation(result);
            evaluation.addErrorsFromErrorStream(errors);
            logger.debug("Created MathEclipseEvaluation with result and errors");

            return evaluation;
        } catch (Exception ex) {
            logger.error("Exception in safeEvaluation(): {}", ex.getMessage(), ex);
            throw ex;
        } finally {
            // Restore the original System.err to avoid affecting other code
            System.setErr(originalErr);
            errorStream.reset();
            logger.debug("Exiting safeEvaluation()");
        }
    }

    /**
     * Performs direct Symja evaluation without validation and error handlers.
     *
     * @param expression expression to validate
     * @return evaluated expression
     */
    private String rawEvaluate(String expression) {
        logger.debug("Entering rawEvaluate() with expression: {}", expression);
        try {
            String result = mathEclipseEvaluator.evaluate(expression).toString();
            logger.debug("rawEvaluate() result: {}", result);
            return result;
        } catch (Exception ex) {
            logger.error("Exception in rawEvaluate(): {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * Formats the result of a mathematical expression into LaTeX format.
     *
     * @param expression the expression to format
     * @return the formatted LaTeX expression
     */
    @Override
    public String formatResult(String expression) {
        logger.info("Entering formatResult() with expression: {}", expression);
        // Placeholder: no transformation currently
        logger.debug("formatResult() returning: {}", expression);
        return expression;
    }

    /**
     * Stops any ongoing evaluations in the Symja evaluator.
     */
    @Override
    public void stopRequest() {
        logger.info("stopRequest() called - stopping ongoing evaluation");
        try {
            mathEclipseEvaluator.stopRequest();
            logger.debug("mathEclipseEvaluator.stopRequest() invoked successfully");
        } catch (Exception ex) {
            logger.error("Exception in stopRequest(): {}", ex.getMessage(), ex);
        }
    }

    /**
     * Resets the internal evaluator to its initial state, removing all variable definitions.
     */
    @Override
    public void clear() {
        logger.info("clear() called - resetting evaluator state");
        try {
            mathEclipseEvaluator = MathEclipseConfig.buildEvalUtilities();
            logger.debug("Evaluator reset successfully");
        } catch (Exception ex) {
            logger.error("Exception in clear(): {}", ex.getMessage(), ex);
        }
    }

    /**
     * Applies initial formatting to the expression, including removing spaces and formatting brackets.
     *
     * @param expression the raw expression to format
     * @return the formatted expression
     */
    private String initialFormatted(String expression) {
        logger.debug("Entering initialFormatted() with expression: {}", expression);
        String noSpaces = removeSpaces(expression);
        logger.debug("Removed spaces: {}", noSpaces);
        String formatted = formatBranches(noSpaces);
        logger.debug("Formatted branches: {}", formatted);
        return formatted;
    }

    /**
     * Wraps an expression with Symja's N[] function for numeric approximation.
     *
     * @param expression input expression
     * @param decimals   number of decimals
     * @return wrapped expression
     */
    public String N(String expression, int decimals) {
        logger.debug("Entering N() with expression: {} and decimals: {}", expression, decimals);
        String wrapped = "N[" + expression + ", " + decimals + "]";
        logger.debug("N() returning: {}", wrapped);
        return wrapped;
    }

    /**
     * Constructs a Symja-compatible Table[] expression.
     *
     * @param function function expression
     * @param variable variable for the table
     * @param origin   lower bound
     * @param bound    upper bound
     * @param points   number of points
     * @return constructed Table expression
     */
    public String Table(String function, String variable, String origin, String bound, String points) {
        logger.debug("Entering Table() with function: {}, variable: {}, origin: {}, bound: {}, points: {}",
                function, variable, origin, bound, points);
        String step = String.format("(%s - %s) / (%s - 1)", bound, origin, points);
        String tableExpr = String.format(
                "Table[{%s, %s /. %s -> %s}, {%s, %s, %s, %s}]",
                variable,             // {x,
                function,             // f(x)
                variable,             // /. x ->
                variable,             // x
                variable,             // {x,
                origin,               // origin
                bound,                // bound
                step                  // step
        );
        logger.debug("Table() returning: {}", tableExpr);
        return tableExpr;
    }

    /**
     * Builds a Symja-compatible Plot[] expression.
     *
     * @param expression the function to plot
     * @param variable   variable of the function
     * @param origin     start of the domain
     * @param bound      end of the domain
     * @return constructed plot expression
     */
    @Deprecated
    public String Plot(String expression, String variable, String origin, String bound) {
        logger.debug("Entering deprecated Plot() with expression: {}, variable: {}, origin: {}, bound: {}",
                expression, variable, origin, bound);
        String plotExpr = "Plot[" + expression + ", {" + variable + ", " + origin + ", " + bound + "}]";
        logger.debug("Plot() returning: {}", plotExpr);
        return plotExpr;
    }

    /**
     * Converts a Symja expression to LaTeX using {@link TeXFormFactory}.
     *
     * @param wolframExpression the expression in Symja syntax
     * @return LaTeX-formatted expression
     */
    private String parseToLateX(String wolframExpression) {
        logger.debug("Entering parseToLateX() with expression: {}", wolframExpression);
        try {
            IExpr expr = createIExpr(wolframExpression);
            StringBuilder sb = new StringBuilder();
            teXParser.convert(sb, expr);
            String latex = sb.toString();
            logger.debug("parseToLateX() returning: {}", latex);
            return latex;
        } catch (Exception ex) {
            logger.error("Exception in parseToLateX(): {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * Parses a string expression into Symja's IExpr representation.
     *
     * @param expression string to parse
     * @return parsed IExpr object
     */
    private IExpr createIExpr(String expression) {
        logger.debug("Entering createIExpr() with expression: {}", expression);
        try {
            ExprEvaluator evaluator = new ExprEvaluator();
            IExpr expr = evaluator.parse(expression);
            logger.debug("createIExpr() parsed expression into IExpr: {}", expr);
            return expr;
        } catch (Exception ex) {
            logger.error("Exception in createIExpr(): {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * Utility method to remove all whitespace characters from an expression.
     *
     * @param expression the original expression
     * @return the expression without spaces
     */
    private String removeSpaces(String expression) {
        logger.debug("Entering removeSpaces() with expression: {}", expression);
        if (expression == null) {
            logger.warn("removeSpaces() received null expression");
            return null;
        }
        String noSpaces = expression.replaceAll("\\s+", "");
        logger.debug("removeSpaces() returning: {}", noSpaces);
        return noSpaces;
    }

    /**
     * Formats square brackets to parentheses for consistency in Symja.
     *
     * @param expression the original expression
     * @return the expression with standardized brackets
     */
    private String formatBranches(String expression) {
        logger.debug("Entering formatBranches() with expression: {}", expression);
        if (expression == null || expression.isBlank()) {
            logger.warn("formatBranches() received null or blank expression");
            return expression;
        }
        String formatted = expression.replace("[", "(").replace("]", ")");
        logger.debug("formatBranches() returning: {}", formatted);
        return formatted;
    }
}
