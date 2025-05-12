package com.placeholder.placeholder.api.facade;

import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.form.tex.TeXFormFactory;
import org.matheclipse.core.interfaces.IExpr;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * {@code MathEclipseFacade} is a class that implements the Facade design pattern
 * in the {@code Symja MathEclipse} mathematical lateXResultEvaluation evaluation and processing library.
 * <p>
 * This class encapsulates and simplifies the library's logic for easy use and implementation.
 * <p>
 * It also implements the lateXResultEvaluation validator and error handler, using both the
 * library's native validator (syntactic) and its own validator (grammatical and semantic).
 *
 * @see MathLibFacade
 * @see MathEclipseExpressionValidator
 */
@Component
public class MathEclipseFacade implements MathLibFacade {

    private final EvalUtilities mathEclipseEvaluator; // Symja native lateXResultEvaluation evaluator
    private final MathEclipseExpressionValidator mathEclipseExpressionValidator; // Custom validator
    private final TeXFormFactory teXParser; // LaTeX parser
    private boolean laTeXFormat;

    // Buffer to capture any errors printed to System.err during evaluation
    private final ByteArrayOutputStream errorStream = new ByteArrayOutputStream();

    public MathEclipseFacade(EvalUtilities mathEclipseEvaluator, MathEclipseExpressionValidator mathEclipseExpressionValidator, TeXFormFactory teXParser) {
        this.mathEclipseEvaluator = mathEclipseEvaluator;
        this.mathEclipseExpressionValidator = mathEclipseExpressionValidator;
        this.teXParser = teXParser;
        this.laTeXFormat = true;
    }

    /**
     * Validates a mathematical lateXResultEvaluation using the custom validator.
     *
     * @param expression the input lateXResultEvaluation
     * @return the original lateXResultEvaluation if valid, or an error message if invalid
     */
    @Override
    public String validate(String expression) {
        return mathEclipseExpressionValidator.validate(
                expression, mathEclipseEvaluator.getEvalEngine()
        );
    }

    /**
     * Evaluates a validated mathematical lateXResultEvaluation.
     *
     * @param expression the lateXResultEvaluation to evaluate
     * @return the result of the evaluation or a formatted error message
     */
    @Override
    public String evaluate(String expression) {
        String validated = validate(expression);
        if (returnIsError(validated)) {
            return validated;
        }

        return safeEvaluate(validated, laTeXFormat);
    }

    /**
     * Performs numeric evaluation of an lateXResultEvaluation with decimal precision.
     *
     * @param expression the input lateXResultEvaluation
     * @param decimals number of decimal places
     * @return the evaluated numeric result or a formatted error message
     */
    @Override
    public String calculate(String expression, int decimals) {
        String validated = validate(expression);
        if (returnIsError(validated)) {
            return validated;
        }

        String numericExpression = N(expression, decimals);
        return safeEvaluate(numericExpression, laTeXFormat);
    }

    /**
     * Constructs a Symja plot lateXResultEvaluation for the given input lateXResultEvaluation.
     *
     * @param expression function to plot
     * @param variable the independent variable (x-axis)
     * @param origin lower bound of the domain
     * @param bound upper bound of the domain
     * @return the result of the plot lateXResultEvaluation evaluation, or an error message
     */
    @Override
    public String draw(String expression, String variable, String origin, String bound) {
        String validated = validate(expression);
        if (returnIsError(validated)) {
            return validated;
        }

        String plotExpression = Plot(expression, variable, origin, bound);
        return safeEvaluate(plotExpression, false); // Do not format plot output
    }

    /**
     * check if the validation has returned an error
     * @param validationResult validation result
     * @return True if validation has returned an error or False if not.
     */
    private boolean returnIsError(String validationResult) {
        return validationResult.startsWith(MathEclipseExpressionValidator.ERROR_SYMBOL);
    }

    /**
     * Safely evaluates an lateXResultEvaluation and captures any warnings/errors.
     *
     * @param expression the lateXResultEvaluation to evaluate
     * @param isFormatted whether to format the result using LaTeX
     * @return the result or formatted error message
     */
    private String safeEvaluate(String expression, boolean isFormatted) {
        System.setErr(new PrintStream(errorStream)); // Redirect System.err to capture evaluation warnings or errors

        try {
            String result = rawEvaluate(expression); // Evaluate the lateXResultEvaluation directly
            String errors = errorStream.toString().trim(); // Read any error messages written during evaluation

            // If no errors, return the result (formatted if requested)
            return errors.isEmpty()
                    ? isFormatted ? formatResult(result) : result
                    : mathEclipseExpressionValidator.formatError(errors);
        } catch (Exception e) {
            // Return a formatted error message if evaluation throws an exception
            return mathEclipseExpressionValidator.formatError("Evaluation failed with exception: " + e.getMessage());
        } finally {
            // Restore the original System.err to avoid affecting other code
            System.setErr(System.err);
        }
    }

    /**
     * Symja evaluation without validation and error handlers
     * @param expression lateXResultEvaluation to validate
     * @return evaluated lateXResultEvaluation
     */
    private String rawEvaluate(String expression) {
        return mathEclipseEvaluator.evaluate(expression).toString();
    }

    @Override
    public String formatResult(String expression) {
        return parseToLateX(expression);
    }

    @Override
    public void stopRequest() {
        mathEclipseEvaluator.stopRequest();
    }

    /**
     * Wraps an lateXResultEvaluation with Symja's N[] function for numeric approximation.
     *
     * @param expression input lateXResultEvaluation
     * @param decimals number of decimals
     * @return wrapped lateXResultEvaluation
     */
    private String N(String expression, int decimals) {
        return "N[" + expression + ", " + decimals + "]";
    }

    /**
     * Builds a Symja-compatible Plot[] lateXResultEvaluation.
     *
     * @param expression the function to plot
     * @param variable variable of the function
     * @param origin start of the domain
     * @param bound end of the domain
     * @return constructed plot lateXResultEvaluation
     */
    private String Plot(String expression, String variable, String origin, String bound) {
        return "Plot[" + expression + ", {" + variable + ", " + origin + ", " + bound + "}]";
    }

    /**
     * Converts a Symja lateXResultEvaluation to LaTeX using {@link TeXFormFactory}.
     *
     * @param wolframExpression the lateXResultEvaluation in Symja syntax
     * @return LaTeX-formatted lateXResultEvaluation
     */
    private String parseToLateX(String wolframExpression) {
        IExpr expr = createIExpr(wolframExpression);
        StringBuilder sb = new StringBuilder();
        teXParser.convert(sb, expr);
        return sb.toString();
    }

    /**
     * Parses a string lateXResultEvaluation into Symja's IExpr representation.
     *
     * @param expression string to parse
     * @return parsed IExpr object
     */
    private IExpr createIExpr(String expression) {
        return new ExprEvaluator().parse(expression);
    }

    public void isLaTeXFormat(boolean laTeXFormat) {
        this.laTeXFormat = laTeXFormat;
    }
}
