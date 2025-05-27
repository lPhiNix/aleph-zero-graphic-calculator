package com.placeholder.placeholder.api.math.regex;

import com.placeholder.placeholder.api.math.enums.validation.constants.MathConstants;
import com.placeholder.placeholder.api.math.validation.symja.validator.MathEclipseExpressionValidator;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * {@code RegexValidator} provides a centralized set of regular expressions
 * and matching utilities to validate different types of mathematical expressions.
 * <p>
 * It includes patterns for variables, constants, function calls, numeric expressions,
 * assignments, equations, Booleans, vectors, and matrices.
 * <p>
 * This class also loads all constant symbols defined in {@link MathConstants}
 * to be recognized in numeric pattern validation.
 *
 * @see MathEclipseExpressionValidator
 */
@Component
public class RegexValidator {
    /**
     * Set of predefined constants (e.g., π, e) allowed in expressions,
     * loaded from {@link MathConstants}.
     */
    private final Set<String> CONSTANTS = Arrays.stream(MathConstants.values())
            .map(MathConstants::getValor)
            .collect(Collectors.toSet());

    /**
     * Pattern to detect individual symbols (e.g., variables and constants) in an expression.
     * Matches words that start with a letter followed by letters or digits.
     */
    public final Pattern SYMBOL_PATTERN = Pattern.compile("\\b([a-zA-Z][a-zA-Z0-9]*)\\b");

    /**
     * Pattern to detect function calls. Matches an identifier followed by
     * an open parenthesis '(' or square bracket '['.
     */
    public final Pattern FUNCTION_CALL_PATTERN = Pattern.compile("([a-zA-Z][a-zA-Z0-9]*)\\s*[(\\[]");

    /**
     * Pattern to validate purely numeric expressions, including arithmetic symbols and constants.
     * Accepts valid decimal numbers, operators, parentheses, and constants defined in {@code CONSTANTS}.
     */
    public final Pattern NUMERIC_PATTERN = Pattern.compile("^(?:[\\d.+\\-*/^()\\s]|\\b(?:" +
            CONSTANTS.stream()
                    .sorted((a, b) -> Integer.compare(b.length(), a.length()))
                    .collect(Collectors.joining("|")) + ")\\b)+$");

    /**
     * Pattern to detect general mathematical functions with at least one letter (e.g. f(x)).
     * Accepts combinations of letters, digits, operators, parentheses, and whitespace.
     */
    public final Pattern FUNCTION_PATTERN = Pattern.compile("(?=.*[a-zA-Z])[-+*/^()a-zA-Z0-9\\s]+");

    /**
     * Pattern to validate assignment expressions (e.g., x = 3).
     * Requires any content on both sides of an equals sign.
     */
    public final Pattern ASSIGNMENT_PATTERN = Pattern.compile(".+=.+");

    /**
     * Pattern to validate equations (e.g., x + 1 == 3).
     * Matches expressions with two sides separated by a double equals sign.
     */
    public final Pattern EQUATION_PATTERN = Pattern.compile(".+==.+");

    /**
     * Pattern to validate Boolean constants (True or False).
     */
    public final Pattern BOOLEAN_PATTERN = Pattern.compile("True|False");

    /**
     * Pattern to validate matrix notation in Symja syntax (e.g., {{1,2},{3,4}}).
     * Accepts matrices of decimal or integer numbers enclosed in nested curly braces.
     */
    public final Pattern MATRIX_PATTERN = Pattern.compile("\\{\\{\\s*-?\\d+(\\.\\d+)?(\\s*,\\s*-?\\d+(\\.\\d+)?)*\\s*}(,\\s*\\{\\s*-?\\d+(\\.\\d+)?(\\s*,\\s*-?\\d+(\\.\\d+)?)*\\s*})+}");

    /**
     * Pattern to validate vector notation in Symja syntax (e.g., {1, 2, 3}).
     * Accepts a single row of numbers separated by commas.
     */
    public final Pattern VECTOR_PATTERN = Pattern.compile("\\{\\s*-?\\d+(\\.\\d+)?(\\s*,\\s*-?\\d+(\\.\\d+)?)*\\s*}");

    /**
     * Validates whether the given expression matches the specified pattern.
     *
     * @param expression the expression to validate
     * @param pattern    the regex pattern to match
     * @return {@code true} if the expression matches the pattern, {@code false} otherwise
     */
    public static boolean match(String expression, Pattern pattern) {
        return pattern.matcher(expression).matches();
    }

    /**
     * Returns a {@link Matcher} for the given expression and pattern,
     * allowing further pattern operations like {@code find()}, {@code group()}, etc.
     *
     * @param expression the expression to search
     * @param pattern    the regex pattern to apply
     * @return the matcher object for the pattern
     */
    public static Matcher matcher(String expression, Pattern pattern) {
        return pattern.matcher(expression);
    }
}
