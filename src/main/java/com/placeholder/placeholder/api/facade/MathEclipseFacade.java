package com.placeholder.placeholder.api.facade;

import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.form.tex.TeXFormFactory;
import org.matheclipse.core.interfaces.IExpr;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

@Component
public class MathEclipseFacade implements MathLibFacade {

    private final EvalUtilities mathEclipseEvaluator;
    private final MathExpressionValidator mathExpressionValidator;
    private final TeXFormFactory teXParser;

    private final ByteArrayOutputStream errorStream = new ByteArrayOutputStream();

    public MathEclipseFacade(EvalUtilities mathEclipseEvaluator, MathExpressionValidator mathExpressionValidator, TeXFormFactory teXParser) {
        this.mathEclipseEvaluator = mathEclipseEvaluator;
        this.mathExpressionValidator = mathExpressionValidator;
        this.teXParser = teXParser;
    }

    @Override
    public String validate(String expression) {
        return mathExpressionValidator.validate(expression, mathEclipseEvaluator.getEvalEngine());
    }

    @Override
    public String evaluate(String expression) {
        String validated = validate(expression);
        if (returnIsError(validated)) {
            return validated;
        }

        return safeEvaluate(validated, true);
    }

    @Override
    public String calculate(String expression, int decimals) {
        String validated = validate(expression);
        if (returnIsError(validated)) {
            return validated;
        }

        String numericExpression = N(expression, decimals);
        return safeEvaluate(numericExpression, true);
    }

    @Override
    public String draw(String expression, String variable, String origin, String bound) {
        String validated = validate(expression);
        if (returnIsError(validated)) {
            return validated;
        }

        String plotExpression = Plot(expression, variable, origin, bound);
        return safeEvaluate(plotExpression, false);
    }

    private boolean returnIsError(String expression) {
        return expression.startsWith(MathExpressionValidator.ERROR_SYMBOL);
    }

    private String safeEvaluate(String expression, boolean isFormatted) {
        System.setErr(new PrintStream(errorStream));

        try {
            String result = rawEvaluate(expression);
            String errors = errorStream.toString().trim();

            return errors.isEmpty()
                    ? isFormatted ? formatResult(result) : result
                    : mathExpressionValidator.formatError(errors);
        } catch (Exception e) {
            return mathExpressionValidator.formatError("Evaluation failed with exception: " + e.getMessage());
        } finally {
            System.setErr(System.err);
        }
    }

    private String rawEvaluate(String expression) {
        return mathEclipseEvaluator.evaluate(expression).toString();
    }

    @Override
    public String formatResult(String expression) {
        return expression;
    }

    private String N(String expression, int decimals) {
        return "N[" + expression + ", " + decimals + "]";
    }

    private String Plot(String expression, String variable, String origin, String bound) {
        return "Plot[" + expression + ", {" + variable + ", " + origin + ", " + bound + "}]";
    }

    private String parseToLateX(String wolframExpression) {
        IExpr expr = createIExpr(wolframExpression);
        StringBuilder sb = new StringBuilder();
        teXParser.convert(sb, expr);
        return sb.toString();
    }

    private IExpr createIExpr(String expression) {
        return new ExprEvaluator().parse(expression);
    }
}
