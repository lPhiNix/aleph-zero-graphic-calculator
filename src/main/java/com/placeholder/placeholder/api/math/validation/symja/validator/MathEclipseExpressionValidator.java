package com.placeholder.placeholder.api.math.validation.symja.validator;

import com.placeholder.placeholder.api.math.enums.validation.constants.MathConstants;
import com.placeholder.placeholder.api.math.enums.validation.functions.Functions;
import com.placeholder.placeholder.api.math.enums.validation.functions.MathFunctions;
import com.placeholder.placeholder.api.math.enums.validation.functions.SymjaFunctions;
import com.placeholder.placeholder.api.math.regex.RegexValidator;
import com.placeholder.placeholder.api.math.validation.symja.annotations.ValidMathEclipseExpression;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    // Whitelist of constants that can be used as-is in expressions without being one-character variables.
    public static final Set<MathConstants> VALID_CONSTANT_WHITELIST = Set.of(MathConstants.values());

    // Whitelist of allowed function names the user can include in their input.
    public static final Set<Functions> VALID_FUNCTION_WHITELIST = Stream.of(MathFunctions.values(), SymjaFunctions.values())
            .flatMap(Arrays::stream)
            .collect(Collectors.toSet());

    // Evaluator used to parse and validate expressions syntactically using MathEclipse.
    private final ExprEvaluator syntaxEvaluator = new ExprEvaluator();

    private final RegexValidator regexValidator;

    @Autowired
    public MathEclipseExpressionValidator(RegexValidator regexValidator) {
        this.regexValidator = regexValidator;
    }

    /**
     * Validates the input expression against grammar, semantics, and syntax.
     *
     * @param expression the mathematical expression to validate.
     * @param context    the validation context to build violation messages.
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
     *
     * @param input   the expression string to check.
     * @param context the validation context to report errors.
     * @return true if all symbols are valid, false otherwise.
     */
    private boolean validateGrammar(String input, ConstraintValidatorContext context) {
        Matcher matcher = regexValidator.SYMBOL_PATTERN.matcher(input); // Find all symbols in the expression
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
     *
     * @param input   the expression string to check.
     * @param context the validation context to report errors.
     * @return true if all function calls are allowed, false otherwise.
     */
    private boolean validateSemantic(String input, ConstraintValidatorContext context) {
        Matcher matcher = regexValidator.FUNCTION_CALL_PATTERN.matcher(input); // Find all function calls
        boolean valid = true;
        // Iterate over each function call found
        while (matcher.find()) {
            String function = matcher.group(1).toLowerCase(); // Extract function name in lowercase
            // Check if function is allowed
            if (
                    VALID_FUNCTION_WHITELIST.stream()
                            .map(Functions::getName)
                            .noneMatch(name -> name.equalsIgnoreCase(function))
            ) {
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
     *
     * @param expression the mathematical expression string.
     * @param context    the validation context to report syntax errors.
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
     * Checks if the symbol is a valid variable (single character and low case).
     *
     * @param symbol the symbol to check.
     * @return true if valid variable, false otherwise.
     */
    private boolean isValidSymbol(String symbol) {
        if (symbol.length() != 1) return false;

        char character = symbol.charAt(0);
        return !Character.isUpperCase(character);
    }

    /**
     * Checks if the symbol is a valid constant from the whitelist.
     *
     * @param symbol the symbol to check.
     * @return true if symbol is a valid constant, false otherwise.
     */
    private boolean isValidConstant(String symbol) {
        return VALID_CONSTANT_WHITELIST
                .stream().anyMatch(
                        constant -> constant.getValor()
                                .equalsIgnoreCase(symbol)
                );
    }

    /**
     * Determines if a symbol is used as a function call in the expression.
     *
     * @param input  the expression string.
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
     *
     * @param errorMessage the original error message.
     * @return a simplified, clear error message.
     */
    private String formatSyntaxErrorMessage(String errorMessage) {

        return errorMessage;
    }
}
