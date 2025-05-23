package com.placeholder.placeholder.api.math.regex;

import com.placeholder.placeholder.api.math.enums.validation.constants.MathConstants;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class RegexValidator {

    private final Set<String> CONSTANTS;

    // Pattern to detect symbols (e.g. variables and constants) in the expression.
    public final Pattern SYMBOL_PATTERN = Pattern.compile("\\b([a-zA-Z][a-zA-Z0-9]*)\\b");

    // Pattern to detect function calls by looking for a name followed by ( or [.
    public final Pattern FUNCTION_CALL_PATTERN = Pattern.compile("([a-zA-Z][a-zA-Z0-9]*)\\s*[(\\[]");

    public final Pattern NUMERIC_PATTERN;
    public final Pattern FUNCTION_PATTERN = Pattern.compile("(?=.*[a-zA-Z])[-+*/^()a-zA-Z0-9\\s]+");
    public final Pattern ASSIGNMENT_PATTERN = Pattern.compile(".+=.+");
    public final Pattern EQUATION_PATTERN = Pattern.compile(".+==.+");
    public final Pattern BOOLEAN_PATTERN = Pattern.compile("True|False");
    public final Pattern MATRIX_PATTERN = Pattern.compile("\\{\\{\\s*-?\\d+(\\.\\d+)?(\\s*,\\s*-?\\d+(\\.\\d+)?)*\\s*}(,\\s*\\{\\s*-?\\d+(\\.\\d+)?(\\s*,\\s*-?\\d+(\\.\\d+)?)*\\s*})+}");
    public final Pattern VECTOR_PATTERN = Pattern.compile("\\{\\s*-?\\d+(\\.\\d+)?(\\s*,\\s*-?\\d+(\\.\\d+)?)*\\s*}");

    public RegexValidator() {
        CONSTANTS = Arrays.stream(MathConstants.values())
                .map(MathConstants::getValor)
                .collect(Collectors.toSet());

        String constantsRegex = CONSTANTS.stream()
                .map(Pattern::quote)
                .collect(Collectors.joining("|"));

        String numberRegex = "-?\\d+(\\.\\d+)?|-?\\d+/\\d+";

        String termRegex = String.format("(%s|%s)", numberRegex, constantsRegex);

        String expressionRegex = String.format("\\s*%s(\\s*[-+*/]\\s*%s)*\\s*", termRegex, termRegex);

        NUMERIC_PATTERN = Pattern.compile(expressionRegex);
    }

    public static boolean match(String expression, Pattern pattern)  {
        return pattern.matcher(expression).matches();
    }

    public static Matcher matcher(String expression, Pattern pattern) {
        return pattern.matcher(expression);
    }
}
