package com.placeholder.placeholder.api.math.validation.symja.validator;

import com.placeholder.placeholder.api.math.validation.symja.annotations.ValidMathEclipseExpression;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@code MathExpressionValidator} is a mathematical expressions validator using both
 * syntactic checks from the MathEclipse library and custom grammatical and semantic
 * rules defined by the application.
 * <p>
 * It includes:
 * <ul>
 *     <li>Validation of variable and symbol names</li>
 *     <li>Whitelist filtering for allowed constants and functions</li>
 *     <li>Syntactic parsing using MathEclipse to catch malformed expressions</li>
 *     <li>A standardized error reporting format</li>
 * </ul>
 */
@Component
public class MathEclipseExpressionValidator implements ConstraintValidator<ValidMathEclipseExpression, String> {

    // Pattern to detect symbols (e.g. variables and constants) in the expression.
    private static final Pattern SYMBOL_PATTERN = Pattern.compile("\\b([a-zA-Z][a-zA-Z0-9]*)\\b");

    // Pattern to detect function calls by looking for a name followed by ( or [.
    private static final Pattern FUNCTION_CALL_PATTERN = Pattern.compile("([a-zA-Z][a-zA-Z0-9]*)\\s*[(\\[]");

    // Whitelist of constants that can be used as-is in expressions without being one-character variables.
    private static final Set<String> VALID_CONSTANT_WHITELIST = Set.of(
            "pi", "infinity", "complexinfinity"
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
     * Validates the input expression against grammar, semantics, and syntax.
     * @param expression the mathematical expression to validate.
     * @param context the validation context to build violation messages.
     * @return true if expression is valid, false otherwise.
     */
    @Override
    public boolean isValid(String expression, ConstraintValidatorContext context) {
        // Check if the expression is null or empty, which is invalid
        if (expression == null || expression.isEmpty()) {
            context.disableDefaultConstraintViolation(); // Disable the default error message
            context.buildConstraintViolationWithTemplate("Expression cannot be null or empty.") // Add custom error message
                    .addConstraintViolation();
            return false;
        }

        boolean isValid = true;

        // Validate grammar (variables/constants)
        if (!validateGrammar(expression, context)) {
            isValid = false;
        }

        // Validate semantic (functions)
        if (!validateSemantic(expression, context)) {
            isValid = false;
        }

        // Validate syntax (using MathEclipse parser)
        if (!validateSyntax(expression, context)) {
            isValid = false;
        }

        return isValid;
    }

    /**
     * Checks if all symbols (variables/constants) in the expression are grammatically valid.
     * @param input the expression string to check.
     * @param context the validation context to report errors.
     * @return true if all symbols are valid, false otherwise.
     */
    private boolean validateGrammar(String input, ConstraintValidatorContext context) {
        Matcher matcher = SYMBOL_PATTERN.matcher(input); // Find all symbols in the expression
        boolean valid = true;
        while (matcher.find()) {
            String symbol = matcher.group(1);

            // Skip if it's a known function call.
            if (isFunctionCall(input, symbol)) continue;

            // Allow if it's a whitelisted constant.
            if (isValidConstant(symbol)) continue;

            // Check if the symbol is a valid variable.
            if (!isValidSymbol(symbol)) { // Check if symbol is a valid variable
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Grammatical Error: Invalid variable name: '" + symbol + "'.")
                        .addConstraintViolation();
                valid = false;
            }
        }
        return valid;
    }

    /**
     * Validates all function calls in the expression against the whitelist of allowed functions.
     * @param input the expression string to check.
     * @param context the validation context to report errors.
     * @return true if all function calls are allowed, false otherwise.
     */
    private boolean validateSemantic(String input, ConstraintValidatorContext context) {
        Matcher matcher = FUNCTION_CALL_PATTERN.matcher(input); // Find all function calls
        boolean valid = true;
        // Iterate over each function call found
        while (matcher.find()) {
            String function = matcher.group(1).toLowerCase(); // Extract function name in lowercase
            if (!VALID_FUNCTION_WHITELIST.contains(function)) { // Check if function is allowed
                context.disableDefaultConstraintViolation(); // Disable default message
                context.buildConstraintViolationWithTemplate("Semantic Error: Invalid function: '" + function + "' is not allowed.") // Custom message
                        .addConstraintViolation();
                valid = false; // Mark as invalid
            }
        }
        return valid;
    }

    /**
     * Parses the expression to check for syntax correctness using MathEclipse.
     * @param expression the mathematical expression string.
     * @param context the validation context to report syntax errors.
     * @return true if syntax is correct, false otherwise.
     */
    private boolean validateSyntax(String expression, ConstraintValidatorContext context) {
        try {
            IExpr expr = syntaxEvaluator.parse(expression);
            if (expr == null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Syntax error found in expression.")
                        .addConstraintViolation();
                return false;
            }
        } catch (Exception e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Syntax Error: " + formatSyntaxErrorMessage(e.getMessage()))
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    /**
     * Checks if the symbol is a valid variable (single character).
     * @param symbol the symbol to check.
     * @return true if valid variable, false otherwise.
     */
    private boolean isValidSymbol(String symbol) {
        return symbol.length() == 1;
    }

    /**
     * Checks if the symbol is a valid constant from the whitelist.
     * @param symbol the symbol to check.
     * @return true if symbol is a valid constant, false otherwise.
     */
    private boolean isValidConstant(String symbol) {
        return VALID_CONSTANT_WHITELIST.contains(symbol.toLowerCase());
    }

    /**
     * Determines if a symbol is used as a function call in the expression.
     * @param input the expression string.
     * @param symbol the symbol to check.
     * @return true if symbol is a function call, false otherwise.
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
     * Cleans and formats the raw syntax error message from MathEclipse to be user-friendly.
     * @param errorMessage the original error message.
     * @return a simplified, clear error message.
     */
    private String formatSyntaxErrorMessage(String errorMessage) {
        String pattern = "Syntax Error: Syntax error in line: \\d+ - Error in .*? (Token:\\d+ \\\\ \\))?\\n(.*?)\\n\\s*\\^";
        Pattern regex = Pattern.compile(pattern, Pattern.DOTALL);
        Matcher matcher = regex.matcher(errorMessage);

        if (matcher.find()) {
            return "Syntax error in: '" + matcher.group(2).trim() + "'";
        }

        return "Syntax error: " + errorMessage;
    }
}
