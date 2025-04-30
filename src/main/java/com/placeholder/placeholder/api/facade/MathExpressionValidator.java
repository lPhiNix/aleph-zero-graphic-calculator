package com.placeholder.placeholder.api.facade;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MathExpressionValidator {

    public static final String ERROR_SYMBOL = "ERROR";

    private static final Pattern CONDITION_PATTERN = Pattern.compile(
            "^([^a-zA-Z]|[a-zA-Z](?![a-zA-Z0-9])|[a-zA-Z][a-zA-Z0-9]*(?=[(\\[])|([a-zA-Z][a-zA-Z0-9]*))*$"
    );

    private static final Pattern FUNCTION_CALL_PATTERN = Pattern.compile("([a-zA-Z][a-zA-Z0-9]*)(?=[(\\[])");
    private static final Pattern SYMBOL_PATTERN = Pattern.compile("\\b([a-zA-Z][a-zA-Z0-9]*)\\b");

    private static final Set<String> VALID_CONSTANTS = Set.of(
            "pi", "phi", "infinity"
    );

    private static final Set<String> VALID_FUNCTIONS = Set.of(
            "d", "integrate", "taylorseries",
            "solve", "limit", "dsolve",
            "primeq", "eigenvalues",
            "inverse", "transpose",
            "gcd", "lcm",
            "simplify", "expand",
            "sqrt", "exp", "log", "log10", "log2", "abs",
            "sin", "cos", "tan", "csc", "cot", "sec",
            "arcsin", "arccos", "arctan", "arccsc", "arccot", "arcsec",
            "sinh", "cosh", "tanh", "coth", "sech", "csch",
            "arcsinh", "arccosh", "arctanh", "arccoth", "arcsech", "arccsch"
    );

    private final ExprEvaluator syntaxEvaluator = new ExprEvaluator();

    public String validate(String expression, EvalEngine engine) {
        expression = expression.replaceAll("\\s+", "");

        String error;

        if ((error = validateConditions(expression)) != null) {
            return formatError(error);
        }

        if ((error = validateFunctions(expression)) != null) {
            return formatError(error);
        }

        if (!validateSyntax(expression, engine)) {
            return formatError("Invalid syntax in expression.");
        }

        return expression;
    }

    private String validateConditions(String input) {
        if (!CONDITION_PATTERN.matcher(input).matches()) {
            return "Expression does not match variable/function structure constraints.";
        }

        Matcher matcher = SYMBOL_PATTERN.matcher(input);
        while (matcher.find()) {
            String symbol = matcher.group(1);
            String lowerSymbol = symbol.toLowerCase();

            // Si parece una función, lo dejamos pasar
            if (input.matches(".*\\b" + Pattern.quote(symbol) + "\\s*[(\\[].*")) {
                continue;
            }

            // Si es una constante válida
            if (VALID_CONSTANTS.contains(lowerSymbol)) {
                continue;
            }

            // Si es una variable pero tiene más de un carácter
            if (symbol.length() > 1) {
                return "Invalid string name: '" + symbol + ".";
            }
        }

        return null;
    }

    private String validateFunctions(String input) {
        Matcher matcher = FUNCTION_CALL_PATTERN.matcher(input);
        while (matcher.find()) {
            String function = matcher.group(1).toLowerCase();

            if (!VALID_FUNCTIONS.contains(function)) {
                return "Invalid function: '" + matcher.group(1) + "' is not allowed.";
            }
        }
        return null;
    }

    private boolean validateSyntax(String expression, EvalEngine engine) {
        try {
            IExpr expr = syntaxEvaluator.parse(expression);
            ExprParser.test(expression, engine);
            return expr != null;
        } catch (Exception e) {
            return false;
        }
    }

    public String formatError(String message) {
        return ERROR_SYMBOL + " " + message;
    }
}