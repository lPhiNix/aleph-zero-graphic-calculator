package com.placeholder.placeholder.api.facade;

import org.matheclipse.core.eval.EvalEngine;
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
        return parseToLateX(expression);
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

    public static void main(String[] args) {
        EvalUtilities evaluator = new EvalUtilities(
                new EvalEngine(
                        "default", 100, null, true
                ), false, false
        );
        MathEclipseFacade facade = new MathEclipseFacade(evaluator, new MathExpressionValidator(), new TeXFormFactory());

        System.out.println("VALIDATE:");
        System.out.println(facade.validate("x^2 + 2*x + 1"));
        System.out.println(facade.validate("Integrate[Sin[x], x]"));
        System.out.println(facade.validate("D[x^3, x]"));
        System.out.println(facade.validate("Solve[x^2 == 1, x]"));

        System.out.println("\nEVALUATE:");
        System.out.println(facade.evaluate("Expand[(x + 1)^2]"));
        System.out.println(facade.evaluate("Simplify[(x^2 - 1)/(x - 1)]"));
        System.out.println(facade.evaluate("Solve[x^2 - 1 == 0, x]"));
        System.out.println(facade.evaluate("Integrate[x^2, x]"));
        System.out.println(facade.evaluate("D[x^3 + 2*x^2 + x, {x, 2}]"));
        System.out.println(facade.evaluate("Limit[(1+1/x)^x, x -> Infinity]"));
        System.out.println(facade.evaluate("Inverse[{{1, 2}, {3, 4}}]"));
        System.out.println(facade.evaluate("Transpose[{{1, 2}, {3, 4}}]"));
        System.out.println(facade.evaluate("Eigenvalues[{{2, 1}, {1, 2}}]"));
        System.out.println(facade.evaluate("PrimeQ[7]"));
        System.out.println(facade.evaluate("GCD[18, 24]"));
        System.out.println(facade.evaluate("LCM[18, 24]"));

        System.out.println("\nCALCULATE (con decimales):");
        System.out.println(facade.calculate("a", 5));
        System.out.println(facade.calculate("E", 10));
        System.out.println(facade.calculate("Sqrt[2]", 8));
        System.out.println(facade.calculate("Log[10]", 6));

        System.out.println("\nDRAW:");
        System.out.println(facade.draw("Sin[x]", "x", "-Pi", "Pi"));
        System.out.println(facade.draw("x^2", "x", "-5", "5"));
        System.out.println(facade.draw("Exp[-x^2]", "x", "-2", "2"));
        System.out.println(facade.draw("Tan[x]", "x", "-Pi/2", "Pi/2"));
        System.out.println(facade.draw("Abs[x]", "x", "-3", "3"));

        System.out.println("INVALID EXPRESSIONS:");

        System.out.println("VALIDATE (deben dar null):");
        System.out.println(facade.validate("xx^2 + 1")); // variable inválida
        System.out.println(facade.validate("Plot[Sin[x], {x, -1, 1}]")); // función no permitida
        System.out.println(facade.validate("Integrate[Sin[x]")); // error de sintaxis

        System.out.println("\nEVALUATE (deben dar null):");
        System.out.println(facade.evaluate("velocity + 3")); // variable inválida
        System.out.println(facade.evaluate("Minimize[x^2 + 1, x]")); // función no permitida
        System.out.println(facade.evaluate("Solve[x^2 == 1, ]")); // error de sintaxis

        System.out.println("\nCALCULATE (con decimales, deben dar null):");
        System.out.println(facade.calculate("Plot[Sin[x]]", 5)); // función no permitida
        System.out.println(facade.calculate("x^^2", 10)); // error de sintaxis
        System.out.println(facade.calculate("Sqrt[longvar]", 8)); // variable inválida

        System.out.println("\nDRAW (deben dar null):");
        System.out.println(facade.draw("x^2 + speed", "x", "-5", "5")); // variable inválida
        System.out.println(facade.draw("Sum[1/x, {x, 1, 10}]", "x", "1", "10")); // función no permitida
        System.out.println(facade.draw("D[x^2,, x]", "x", "-2", "2")); // error de sintaxis

    }
}
