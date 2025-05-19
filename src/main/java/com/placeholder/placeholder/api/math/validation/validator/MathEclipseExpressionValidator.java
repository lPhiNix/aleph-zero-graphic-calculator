package com.placeholder.placeholder.api.math.validation.validator;

import com.placeholder.placeholder.api.math.exceptions.MathGrammaticalException;
import com.placeholder.placeholder.api.math.exceptions.MathSemanticException;
import com.placeholder.placeholder.api.math.exceptions.MathSyntaxException;
import com.placeholder.placeholder.api.math.validation.annotations.ValidMathEclipseExpression;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.SneakyThrows;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MathEclipseExpressionValidator implements ConstraintValidator<ValidMathEclipseExpression, String> {

    // Pattern to detect symbols (e.g. variables and constants) in the expression.
    private static final Pattern SYMBOL_PATTERN = Pattern.compile("\\b([a-zA-Z][a-zA-Z0-9]*)\\b");

    // Pattern to detect function calls by looking for a name followed by ( or [.
    private static final Pattern FUNCTION_CALL_PATTERN = Pattern.compile("([a-zA-Z][a-zA-Z0-9]*)\\s*[(\\[]");

    // Whitelist of constants that can be used as-is in expressions without being one-character variables.
    private static final Set<String> VALID_CONSTANT_WHITELIST = Set.of(
            "pi", "phi", "infinity", "complexinfinity"
    );

    // Whitelist of allowed function names the user can include in their input.
    private static final Set<String> VALID_FUNCTION_WHITELIST = Set.of(
            "gamma", "zeta", "erf", "fresnelc", "c",
            "d", "diff", "integrate", "taylor", "solve", "limit", "dsolve", "logicalexpand",
            "dot", "cross", "norm", "normalize", "vectorangle", "projection",
            "eigenvalues", "inverse", "transpose",
            "gcd", "lcm", "simplify", "expand", "sqrt", "exp", "log", "log10", "log2", "abs",
            "sin", "cos", "tan", "csc", "cot", "sec",
            "arcsin", "arccos", "arctan", "arccsc", "arccot",
            "arcsec", "sinh", "cosh", "tanh", "coth", "sech", "csch",
            "arcsinh", "arccosh", "arctanh", "arccoth", "arcsech", "arccsch"
    );

    // Evaluator used to parse and validate expressions syntactically using MathEclipse.
    private final ExprEvaluator syntaxEvaluator = new ExprEvaluator();

    /**
     * Validates a full mathematical expression using custom grammar, allowed functions/variables,
     * and MathEclipse's parser to ensure both syntactic and semantic correctness.
     *
     * @param expression the input expression to validate
     * @param context evaluation context without pretext
     * @return the input expression if valid; otherwise, a string beginning with "ERROR" followed by the cause
     */
    @SneakyThrows
    @Override
    public boolean isValid(String expression, ConstraintValidatorContext context) {
        if (expression == null || expression.isEmpty()) {
            return false;
        }

        // Validate symbols (variables/constants).
        validateGrammar(expression);

        // Validate function calls.
        validateSemantic(expression);

        // Validate syntax using MathEclipse.
        validateSyntax(expression);

        return true;
    }

    /**
     * Validates the individual symbols found in the expression,
     * including variables and constants, ensuring they conform to allowed rules.
     *
     * @param input the expression with no whitespace
     * @return null if valid, or an error message describing the invalid symbol
     */
    private void validateGrammar(String input) throws MathGrammaticalException {
        Matcher matcher = SYMBOL_PATTERN.matcher(input);
        while (matcher.find()) {
            String symbol = matcher.group(1);

            // Skip if it's a known function call.
            if (isFunctionCall(input, symbol)) continue;

            // Allow if it's a whitelisted constant.
            if (isValidConstant(symbol)) continue;

            // Check if the symbol is a valid variable.
            if (!isValidSymbol(symbol)) {
                throw new MathGrammaticalException("Invalid variable name: '" + symbol + "'.");
            }
        }
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
        return VALID_CONSTANT_WHITELIST.contains(symbol.toLowerCase());
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
    private void validateSemantic(String input) throws MathSemanticException {
        Matcher matcher = FUNCTION_CALL_PATTERN.matcher(input);
        while (matcher.find()) {
            String function = matcher.group(1).toLowerCase();
            if (!VALID_FUNCTION_WHITELIST.contains(function)) {
                throw new MathSemanticException("Invalid function: '" + function + "' is not allowed.");
            }
        }
    }

    /**
     * Uses MathEclipse to parse the expression and ensure it is syntactically correct.
     *
     * @param expression the cleaned expression
     * @return true if the expression parses correctly, false if a syntax error occurs
     */
    private void validateSyntax(String expression) throws MathSyntaxException {
        try {
            IExpr expr = syntaxEvaluator.parse(expression);

            if (expr == null) {
                throw new MathSyntaxException("Syntax error in expression: '" + expression + "'.");
            }
        } catch (Exception e) {
            throw new MathSyntaxException("Syntax error in expression: " + e.getMessage());
        }
    }
}
