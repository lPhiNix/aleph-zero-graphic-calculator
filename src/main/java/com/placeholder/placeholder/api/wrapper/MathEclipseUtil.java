package com.placeholder.placeholder.api.wrapper;

import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.form.tex.TeXFormFactory;
import org.matheclipse.core.interfaces.IExpr;
import org.springframework.stereotype.Component;

@Component
public class MathEclipseUtil {

    private final TeXFormFactory teXParser;

    public MathEclipseUtil() {
        this.teXParser = new TeXFormFactory();
    }

    public String parseToLateX(String wolframExpression) {
        IExpr expr = createIExpr(wolframExpression);
        StringBuilder sb = new StringBuilder();
        teXParser.convert(sb, expr);
        return sb.toString();
    }

    public IExpr createIExpr(String expression) {
        return new ExprEvaluator().parse(expression);
    }
}