package com.placeholder.placeholder.api.facade;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@code MathExpressionValidator} is a Spring component that validates mathematical
 * expressions using both syntactic checks from the MathEclipse library and custom
 * grammatical and semantic rules defined by the application.
 * <p>
 * It includes:
 * <ul>
 *     <li>Validation of variable and symbol names</li>
 *     <li>Whitelist filtering for allowed constants and functions</li>
 *     <li>Syntactic parsing using MathEclipse to catch malformed expressions</li>
 *     <li>A standardized error reporting format</li>
 * </ul>
 *
 * @see MathEclipseFacade
 * @see MathLibFacade
 */
@Component
public class MathExpressionValidator {

    // Special prefix used to indicate an error in expression validation.
    public static final String ERROR_SYMBOL = "ERROR";

    // Pattern to detect symbols (e.g. variables and constants) in the expression.
    private static final Pattern SYMBOL_PATTERN = Pattern.compile("\\b([a-zA-Z][a-zA-Z0-9]*)\\b");

    // Pattern to detect function calls by looking for a name followed by ( or [.
    private static final Pattern FUNCTION_CALL_PATTERN = Pattern.compile("([a-zA-Z][a-zA-Z0-9]*)\\s*[(\\[]");

    // Whitelist of constants that can be used as-is in expressions without being one-character variables.
    private static final Set<String> VALID_CONSTANTS = Set.of("pi", "phi", "infinity");

    // Whitelist of allowed function names the user can include in their input.
    private static final Set<String> VALID_FUNCTIONS = Set.of(
            "d", "integrate", "taylorseries", "solve", "limit", "dsolve",
            "primeq", "eigenvalues", "inverse", "transpose", "gcd", "lcm",
            "simplify", "expand", "sqrt", "exp", "log", "log10", "log2", "abs",
            "sin", "cos", "tan", "csc", "cot", "sec",
            "arcsin", "arccos", "arctan", "arccsc", "arccot", "arcsec",
            "sinh", "cosh", "tanh", "coth", "sech", "csch",
            "arcsinh", "arccosh", "arctanh", "arccoth", "arcsech", "arccsch"
    );

    // Evaluator used to parse and validate expressions syntactically using MathEclipse.
    private final ExprEvaluator syntaxEvaluator = new ExprEvaluator();

    /**
     * Validates a full mathematical expression using custom grammar, allowed functions/variables,
     * and MathEclipse's parser to ensure both syntactic and semantic correctness.
     *
     * @param expression the input expression to validate
     * @param engine     the evaluation engine used for parsing validation
     * @return the input expression if valid; otherwise, a string beginning with "ERROR" followed by the cause
     */
    public String validate(String expression, EvalEngine engine) {
        // Remove all whitespaces to simplify token parsing.
        expression = removeSpaces(expression);

        String error;

        // Validate that all symbols (variables/constants) are valid.
        if ((error = validateSymbols(expression)) != null) {
            return formatError(error);
        }

        // Validate that all function calls use only allowed functions.
        if ((error = validateFunctionCalls(expression)) != null) {
            return formatError(error);
        }

        // Use MathEclipse parser to ensure the expression is syntactically valid.
        if (!validateSyntax(expression, engine)) {
            return formatError("Invalid syntax in expression.");
        }

        // If all checks pass, return the expression as valid.
        return expression;
    }

    /**
     * Validates the individual symbols found in the expression,
     * including variables and constants, ensuring they conform to allowed rules.
     *
     * @param input the expression with no whitespace
     * @return null if valid, or an error message describing the invalid symbol
     */
    private String validateSymbols(String input) {
        Matcher matcher = SYMBOL_PATTERN.matcher(input);
        while (matcher.find()) {
            String symbol = matcher.group(1);

            // Skip symbol if it's a known function call
            if (isFunctionCall(input, symbol)) {
                continue;
            }

            // Allow symbol if it's a whitelisted constant
            if (isValidConstant(symbol)) {
                continue;
            }

            // If the symbol is not a single character, it is invalid
            if (!isValidSymbol(symbol)) {
                return "Invalid string name: '" + symbol + "'.";
            }
        }
        return null;
    }

    /**
     * Checks if a symbol is a valid variable name.
     * Only single-character variables are allowed.
     *
     * @param symbol the symbol to check
     * @return true if valid, false otherwise
     */
    private boolean isValidSymbol(String symbol) {
        return symbol.length() == 1;
    }

    /**
     * Checks if a symbol is a predefined constant like "pi" or "infinity".
     *
     * @param symbol the symbol to check
     * @return true if it is a valid constant
     */
    private boolean isValidConstant(String symbol) {
        return VALID_CONSTANTS.contains(symbol.toLowerCase());
    }

    /**
     * Determines if a symbol is a function by checking if it appears
     * followed by an open parenthesis or bracket (used for function calls).
     *
     * @param input  the full expression
     * @param symbol the symbol to search for
     * @return true if the symbol is used as a function call
     */
    private boolean isFunctionCall(String input, String symbol) {
        int index = input.indexOf(symbol);
        while (index != -1) {
            int nextCharIndex = index + symbol.length();
            if (nextCharIndex < input.length()) {
                char nextChar = input.charAt(nextCharIndex);
                if (nextChar == '(' || nextChar == '[') {
                    return true;
                }
            }
            index = input.indexOf(symbol, index + 1);
        }
        return false;
    }

    /**
     * Validates that all function calls in the expression are whitelisted.
     *
     * @param input the expression to scan
     * @return null if all function calls are valid, otherwise an error message
     */
    private String validateFunctionCalls(String input) {
        Matcher matcher = FUNCTION_CALL_PATTERN.matcher(input);
        while (matcher.find()) {
            String function = matcher.group(1).toLowerCase();
            if (!VALID_FUNCTIONS.contains(function)) {
                return "Invalid function: '" + matcher.group(1) + "' is not allowed.";
            }
        }
        return null;
    }

    /**
     * Uses MathEclipse to parse the expression and ensure it is syntactically correct.
     *
     * @param expression the cleaned expression
     * @param engine     the evaluation engine from MathEclipse
     * @return true if the expression parses correctly, false if a syntax error occurs
     */
    private boolean validateSyntax(String expression, EvalEngine engine) {
        try {
            IExpr expr = syntaxEvaluator.parse(expression);
            ExprParser.test(expression, engine);
            return expr != null;
        } catch (Exception e) {
            return false;
        }
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
     * Formats an error message by prepending the standard error symbol.
     *
     * @param message the detailed error message
     * @return a full error string to be returned
     */
    public String formatError(String message) {
        return ERROR_SYMBOL + " " + message;
    }
}