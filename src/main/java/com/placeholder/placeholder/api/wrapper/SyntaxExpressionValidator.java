package com.placeholder.placeholder.api.wrapper;

import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SyntaxExpressionValidator {

    private static final String CONDITION_PATTERN = "^([^a-zA-Z]|[a-zA-Z](?![a-zA-Z])|[a-zA-Z]+(?=[(\\[]))*$";
    private static final String VALIDATION_FUNCTIONS_PATTERN = "([a-zA-Z]+)(?=[(\\[])";

    private static final Set<String> VALID_FUNCTIONS = new HashSet<>(Arrays.asList(
            "d", "integrate", "taylorseries",
            "simplify", "expand",
            "sqrt", "exp", "log", "log10",
            "sin", "cos", "tan", "csc", "cot", "sec",
            "arcsin", "arccos", "arctan", "arccsc", "arccot", "arcsec",
            "sinh", "cosh", "tanh", "coth", "sech", "csch",
            "arcSinh", "arcCosh", "arcTanh", "arcCoth", "arcSech", "arcCsch"
    ));

    private final ExprEvaluator syntaxEvaluator;

    public SyntaxExpressionValidator() {
        this.syntaxEvaluator = new ExprEvaluator();
    }

    public boolean validate(String expression) {
        expression = removeSpaces(expression);

        if (!validateConditions(expression)) {
            System.out.println("C");
            return false;
        }

        /*
        if (!validateFunctions(expression)) {
            return false;
        }
         */

        if (!validateSyntax(expression)) {
            System.out.println("S");
            return false;
        }

        return true;
    }

    private String removeSpaces(String expression) {
        return expression.replaceAll("\\s+", "");
    }

    private boolean validateConditions(String input) {
        return input.matches(CONDITION_PATTERN);
    }

    private boolean validateFunctions(String input) {
        Pattern pattern = Pattern.compile(VALIDATION_FUNCTIONS_PATTERN);
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            String word = matcher.group(1).toLowerCase();
            System.out.println(word);
            if (!VALID_FUNCTIONS.contains(word)) {
                return false;
            }
        }
        return true;
    }

    private boolean validateSyntax(String expression) {
        try {
            IExpr expr = syntaxEvaluator.parse(expression);
            return expr != null;
        } catch (Exception e) {
            return false;
        }
    }
}