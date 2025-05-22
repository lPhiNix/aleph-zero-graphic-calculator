package com.placeholder.placeholder.api.math.regex;

import com.placeholder.placeholder.api.math.enums.validation.constants.MathConstants;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class RegexValidator {

    private final String CONSTANTS_PATTERN;

    // Pattern to detect symbols (e.g. variables and constants) in the expression.
    public final Pattern SYMBOL_PATTERN = Pattern.compile("\\b([a-zA-Z][a-zA-Z0-9]*)\\b");

    // Pattern to detect function calls by looking for a name followed by ( or [.
    public final Pattern FUNCTION_CALL_PATTERN = Pattern.compile("([a-zA-Z][a-zA-Z0-9]*)\\s*[(\\[]");

    public final Pattern FUNCTION_PATTERN;
    public final Pattern ASSIGNMENT_PATTERN;

    public final Pattern EQUATION_PATTERN = Pattern.compile(".+==.+");
    private final Pattern BOOLEN_PATTERN = null;

    public final Pattern NUMERIC_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?([+-]\\d+i)?|-\\d+i|\\d+/\\d+");
    public final Pattern MATRIX_PATTERN = Pattern.compile("\\{\\{\\s*-?\\d+(\\.\\d+)?(\\s*,\\s*-?\\d+(\\.\\d+)?)*\\s*}(,\\s*\\{\\s*-?\\d+(\\.\\d+)?(\\s*,\\s*-?\\d+(\\.\\d+)?)*\\s*})+}");
    public final Pattern VECTOR_PATTERN = Pattern.compile("\\{\\s*-?\\d+(\\.\\d+)?(\\s*,\\s*-?\\d+(\\.\\d+)?)*\\s*}");
    public final Pattern BOOLEAN_PATTERN = Pattern.compile("\\s*(-?\\d+(\\.\\d+)?|\\d+/\\d+)\\s*=\\s*(-?\\d+(\\.\\d+)?|\\d+/\\d+)\\s*");

    public RegexValidator() {
        // Construimos un patrón para constantes como: Pi|E|I|GoldenRatio|infinity|...
        CONSTANTS_PATTERN = Arrays.stream(MathConstants.values())
                .map(MathConstants::getValor)
                .collect(Collectors.joining("|", "(", ")"));

        FUNCTION_PATTERN = Pattern.compile(
                "(?i)(?!" +
                        CONSTANTS_PATTERN.substring(
                                1, CONSTANTS_PATTERN.length() -1
                        ) +
                        "$)[a-zA-Z][a-zA-Z0-9]*\\s*[(\\[]"
        );

        ASSIGNMENT_PATTERN = Pattern.compile("\\s*[a-z]\\s*=\\s*.+");

    }
}
