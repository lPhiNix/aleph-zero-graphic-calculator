package com.placeholder.placeholder.api.math.facade.symja;

import com.placeholder.placeholder.api.math.facade.MathLibFacade;
import com.placeholder.placeholder.api.math.facade.symja.exceptions.MathEclipseEvaluationException;
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
 * @see MathEclipseExpressionValidator
 */
@Component
public class MathEclipseFacade implements MathLibFacade<MathEclipseEvaluation> {

    private EvalUtilities mathEclipseEvaluator; // Symja native expression evaluator
    private final MathEclipseExpressionValidator mathEclipseExpressionValidator; // Custom validator
    private final TeXFormFactory teXParser; // LaTeX parser

    // Buffer to capture any errors printed to System.err during evaluation
    private final ByteArrayOutputStream errorStream = new ByteArrayOutputStream();

    public MathEclipseFacade(
            EvalUtilities mathEclipseEvaluator, 
            MathEclipseExpressionValidator mathEclipseExpressionValidator,
            TeXFormFactory teXParser
    ) {
        this.mathEclipseEvaluator = mathEclipseEvaluator;
        this.mathEclipseExpressionValidator = mathEclipseExpressionValidator;
        this.teXParser = teXParser;
    }

    /**
     * Validates a mathematical expression using the custom validator.
     *
     * @param expression the input expression
     * @return the original expression if valid, or an error message if invalid
     */
    @Override
    public String validate(String expression) {
        return mathEclipseExpressionValidator.validate(expression);
    }

    /**
     * Evaluates a validated mathematical expression.
     *
     * @param expression the expression to evaluate
     * @return the result of the evaluation or a formatted error message
     */
    @Override
    public MathEclipseEvaluation evaluate(String expression) {
        String validatedExpression = validate(expression);
        return safeEvaluate(validatedExpression);
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
        String validatedExpression = validate(expression);
        String numericExpression = N(validatedExpression, decimals);
        return safeEvaluate(numericExpression);
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
        String validatedExpression = validate(expression);
        String plotExpression = Plot(validatedExpression, variable, origin, bound);
        return safeEvaluate(plotExpression); // Do not format plot output
    }

    /**
     * Safely evaluates an expression and captures any warnings/errors.
     *
     * @param expression the expression to evaluate
     * @return the result or formatted error message
     */
    private MathEclipseEvaluation safeEvaluate(String expression) {
        System.setErr(new PrintStream(errorStream)); // Redirect System.err to capture evaluation warnings or errors
        try {
            String result = rawEvaluate(expression); // Evaluate the expression directly
            String errors = errorStream.toString().trim(); // Read any error messages written during evaluation

            System.out.println(errors); // TODO

            MathEclipseEvaluation evaluation = new MathEclipseEvaluation(result);
            evaluation.addErrorsFromErrorStream(errors);

            return evaluation;
        } catch (Exception e) {
            throw new MathEclipseEvaluationException(e.getMessage(), e);
        } finally {
            // Restore the original System.err to avoid affecting other code
            System.setErr(System.err);
            errorStream.reset();
            resetEvaluator();
        }
    }

    /**
     * Symja evaluation without validation and error handlers
     * @param expression expression to validate
     * @return evaluated expression
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

    private void resetEvaluator() {
        mathEclipseEvaluator = MathEclipseConfig.buildEvalUtilities();
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
}
