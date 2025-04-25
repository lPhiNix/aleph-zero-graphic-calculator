package com.placeholder.placeholder.api.wrapper;

import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.springframework.stereotype.Component;

@Component
public class MathEclipseWrapper {
    private final ExprEvaluator mathEclipse;

    private final MathEclipseUtil util;
    private final SyntaxExpressionValidator validator;

    public MathEclipseWrapper(MathEclipseUtil util, SyntaxExpressionValidator validator) {
        mathEclipse = new ExprEvaluator();
        this.util = util;
        this.validator = validator;

        F.initSymbols();
    }

    public boolean validate(String expression) {
        return validator.validate(expression);
    }

    public void evaluate(String expression) {

    }

    public String process(String expression) {
        if (!validate(expression)) {
            return null;
        }

        return mathEclipse.eval(expression).toString();
    }

    public void calculate(String expression) {

    }

    public void draw(String expression) {

    }

    private String N(String expression, int decimals) {
        return "N[" + expression + ", " + decimals + "]";
    }

    public String Solve(String expression, String... variables) {
        String variableList = String.join(", ", variables);
        return "Solve[" + expression + ", {" + variableList + "}]";
    }

    private String Plot(String expression, String variable, String origin, String bound) {
        return "Plot[" + expression + ", {" + variable + ", " + origin + ", " + bound + "}]";
    }

    public static void main(String[] args) {
        MathEclipseUtil util = new MathEclipseUtil();
        SyntaxExpressionValidator validator = new SyntaxExpressionValidator();
        MathEclipseWrapper mathEclipseWrapper = new MathEclipseWrapper(util, validator);

        System.out.println(mathEclipseWrapper.Solve("x^2 + Sin[x] == 0", "x", "y"));

        //System.out.println(mathEclipseWrapper.process("Plot[Sin[1], {x, -Pi, Pi}]"));
        //System.out.println(mathEclipseWrapper.process("Simplify[(x^2 - 1)/(x - 1)]"));
        //System.out.println(mathEclipseWrapper.process("Expand[(x + 1)^2]"));
        //System.out.println(mathEclipseWrapper.process("FullSimplify[Sin[x]^2 + Cos[x]^2]"));
        //System.out.println(mathEclipseWrapper.process("Solve[x^2 + Sin[x] == 0, x]"));
        //System.out.println(mathEclipseWrapper.process("D[x^2 + y^2, x]"));
        //System.out.println(mathEclipseWrapper.process("D[x^3 + 2*x^2 + x, {x, 2}]"));
        //System.out.println(mathEclipseWrapper.process("Integrate[Integrate[x*y, x], y]"));
        //System.out.println(mathEclipseWrapper.process("Solve[{x^2 + y^2 == 1, x + y == 1}, {x, y}]"));
        //System.out.println(mathEclipseWrapper.process("DSolve[y''[x] + y[x] == 0, y[x], x]"));
        //System.out.println(mathEclipseWrapper.process("Inverse[{{1, 2}, {3, 4}}]"));
        //System.out.println(mathEclipseWrapper.process("SingularValueDecomposition[{{1, 2}, {3, 4}}]"));
        //System.out.println(mathEclipseWrapper.process("Solve[{{1, 2}, {3, 4}}.{x, y} == {5, 6}, {x, y}]"));
        //System.out.println(mathEclipseWrapper.process("BesselJ[1, x]"));
        //System.out.println(mathEclipseWrapper.process("PrimeQ[7]"));
        //System.out.println(mathEclipseWrapper.process("FourierTransform[Sin[x], x, k]"));
        //System.out.println(mathEclipseWrapper.process("Minimize[x^2 + 2*x + 1, x]"));
        //System.out.println(mathEclipseWrapper.process("Plot[Sin[x], {x, -Pi, Pi}]"));
        //System.out.println(mathEclipseWrapper.process("2 * Meter + 3 * Centimeter"));
        //System.out.println(mathEclipseWrapper.process("PDF[NormalDistribution[0, 1], 1]"));
        //System.out.println(mathEclipseWrapper.process("FindFit[{{1, 2}, {2, 3}, {3, 5}}, {a x + b}, {x}, {a, b}]"));
    }
}
