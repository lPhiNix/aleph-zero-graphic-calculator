package com.placeholder.placeholder.api.math.validation.symja.validator;

import com.placeholder.placeholder.api.math.validation.symja.annotations.ValidMathEclipseExpression;
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

    @SneakyThrows
    @Override
    public boolean isValid(String expression, ConstraintValidatorContext context) {
        if (expression == null || expression.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Expression cannot be null or empty.")
                    .addConstraintViolation();
            return false;
        }

        boolean isValid = true;

        // Validate symbols (variables/constants).
        if (!validateGrammar(expression, context)) {
            isValid = false;
        }

        // Validate function calls.
        if (!validateSemantic(expression, context)) {
            isValid = false;
        }

        // Validate syntax using MathEclipse.
        if (!validateSyntax(expression, context)) {
            isValid = false;
        }

        return isValid;
    }

    private boolean validateGrammar(String input, ConstraintValidatorContext context) {
        Matcher matcher = SYMBOL_PATTERN.matcher(input);
        boolean valid = true;
        while (matcher.find()) {
            String symbol = matcher.group(1);

            // Skip if it's a known function call.
            if (isFunctionCall(input, symbol)) continue;

            // Allow if it's a whitelisted constant.
            if (isValidConstant(symbol)) continue;

            // Check if the symbol is a valid variable.
            if (!isValidSymbol(symbol)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Grammatical Error: Invalid variable name: '" + symbol + "'.")
                        .addConstraintViolation();
                valid = false;
            }
        }
        return valid;
    }

    private boolean validateSemantic(String input, ConstraintValidatorContext context) {
        Matcher matcher = FUNCTION_CALL_PATTERN.matcher(input);
        boolean valid = true;
        while (matcher.find()) {
            String function = matcher.group(1).toLowerCase();
            if (!VALID_FUNCTION_WHITELIST.contains(function)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Semantic Error: Invalid function: '" + function + "' is not allowed.")
                        .addConstraintViolation();
                valid = false;
            }
        }
        return valid;
    }

    private boolean validateSyntax(String expression, ConstraintValidatorContext context) {
        try {
            IExpr expr = syntaxEvaluator.parse(expression);
            if (expr == null) {
                throw new Exception("Null expression");
            }
        } catch (Exception e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Syntax Error: " + formatSyntaxErrorMessage(e.getMessage()))
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean isValidSymbol(String symbol) {
        return symbol.length() == 1;
    }

    private boolean isValidConstant(String symbol) {
        return VALID_CONSTANT_WHITELIST.contains(symbol.toLowerCase());
    }

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
     * Extracts the relevant part of a MathEclipse syntax error message.
     *
     * @param errorMessage The full error message from MathEclipse.
     * @return The cleaned, user-friendly error message.
     */
    private String formatSyntaxErrorMessage(String errorMessage) {
        // Regex to capture the line causing the error.
        String pattern = "Syntax Error: Syntax error in line: \\d+ - Error in .*? (Token:\\d+ \\\\ \\))?\\n(.*?)\\n\\s*\\^";
        Pattern regex = Pattern.compile(pattern, Pattern.DOTALL);
        Matcher matcher = regex.matcher(errorMessage);

        if (matcher.find()) {
            // Return the problematic line without leading/trailing whitespace.
            return "Syntax error in: '" + matcher.group(2).trim() + "'";
        }

        // Fallback if the message doesn't match the expected pattern.
        return "Syntax error: " + errorMessage;
    }
}
