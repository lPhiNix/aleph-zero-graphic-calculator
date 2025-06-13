package com.alephzero.alephzero.api.math.service.memory;

import com.alephzero.alephzero.api.math.enums.validation.constants.MathConstants;
import com.alephzero.alephzero.api.math.regex.RegexValidator;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@code MathAssignmentMemory} is a memory buffer component that manages assignment
 * of symbolic variables and functions in mathematical expressions.
 * <p>
 * It allows storing and retrieving simple one-character variable definitions (e.g., {@code x=5}),
 * as well as function definitions with lowercase letters, parentheses or square brackets (e.g., {@code f(x)=2*x+1}, {@code g[y]=y^2}).
 * It replaces these variables/functions dynamically during expression evaluation. It also loads constant values
 * at initialization and validates symbol usage.
 * <p>
 * This service is especially useful for preserving user-defined variables and functions across requests.
 */
@Component
public class MathAssignmentMemory {

    // Pattern for variable or function assignment (supports: x=expr, f(x)=expr, g[y]=expr)
    // LHS: either a single lowercase letter, or a lowercase function name with () or []
    private static final Pattern ASSIGNMENT_PATTERN = Pattern.compile("^(([a-z])|([a-z]+\\s*\\(\\s*[a-z]\\s*\\))|([a-z]+\\s*\\[\\s*[a-z]\\s*]))=(.+)$");

    private final Map<String, String> variableMap = new HashMap<>(); // Stores variable and function assignments
    private final RegexValidator regexValidator; // Regex-based validator for symbol recognition

    /**
     * Constructor that injects the regex validator and initializes constants.
     *
     * @param regexValidator the validator used to extract symbols in expressions
     */
    public MathAssignmentMemory(RegexValidator regexValidator) {
        this.regexValidator = regexValidator;
        init();
    }

    /**
     * Initializes the memory with predefined mathematical constants
     * from {@link MathConstants}.
     */
    private void init() {
        MathConstants[] constants = MathConstants.values();

        for (MathConstants constant : constants) {
            if (constant.getNativeValor() != null) {
                addVariable(constant.getCustomValor(), constant.getNativeValor());
            }
        }
    }

    /**
     * Adds or replaces a variable or function assignment in memory.
     *
     * @param variable the variable or function symbol (e.g., "x", "f(x)", "g[y]")
     * @param value the value assigned to the variable or function (e.g., "3", "2*x+1")
     * @return the previous value associated with the variable, or {@code null} if none
     */
    private String addVariable(String variable, String value) {
        return variableMap.put(variable.replaceAll("\\s+", ""), value); // Normalize spaces out of keys
    }

    /**
     * Processes a mathematical expression. If it is an assignment (e.g., "x=2" or "f(x)=2*x+1"),
     * it stores the variable or function. Otherwise, it replaces known variables/functions in the expression.
     *
     * @param expression the input expression
     * @return a confirmation of assignment or the updated expression with variables/functions replaced
     */
    public String process(String expression) {
        init();

        Matcher matcher = ASSIGNMENT_PATTERN.matcher(expression.replaceAll("\\s+", "")); // Remove all spaces for matching
        if (matcher.matches()) {
            String lhs = matcher.group(1);
            String value = matcher.group(5);
            addVariable(lhs, value);
            System.out.println(lhs + " = " + value);
            return lhs + " = " + value;
        }
        for (String s : variableMap.keySet()) {
            System.out.println(s + " + " + variableMap.get(s));
        }
        return replaceVariables(expression);
    }

    /**
     * Replaces all known variable or function symbols in the given expression with their assigned values.
     * Functions are replaced first, then variables.
     *
     * @param expression the original expression
     * @return the expression with variables/functions replaced by their stored values
     */
    public String replaceVariables(String expression) {
        String result = expression;
        // Sort keys by length descending to avoid partial replacements (e.g., f(x) before f)
        List<String> keys = new ArrayList<>(variableMap.keySet());
        keys.sort((a, b) -> Integer.compare(b.length(), a.length()));
        for (String key : keys) {
            String regexKey = Pattern.quote(key);
            // For function, allow optional spaces inside the signature
            if (key.matches("^[a-z]+\\([a-z]\\)$")) {
                // f(x), match f\\s*\\(\\s*x\\s*\\)
                String funPattern = key.replaceAll("([a-z]+)\\(([a-z])\\)", "$1\\\\s*\\\\(\\\\s*$2\\\\s*\\\\)");
                result = result.replaceAll(funPattern, variableMap.get(key));
            } else if (key.matches("^[a-z]+\\[[a-z]]$")) {
                // g[y], match g\\s*\\[\\s*y\\s*\\]
                String funPattern = key.replaceAll("([a-z]+)\\[([a-z])]", "$1\\\\s*\\\\[\\\\s*$2\\\\s*\\\\]");
                result = result.replaceAll(funPattern, variableMap.get(key));
            } else {
                // Single variable
                result = result.replaceAll("\\b" + regexKey + "\\b", variableMap.get(key));
            }
        }
        return result;
    }

    /**
     * Clears all user-defined variables and functions from memory.
     */
    public void clear() {
        variableMap.clear();
    }

    /**
     * Retrieves a copy of all current variable assignments.
     *
     * @return an unmodifiable map of variable/function-value pairs
     */
    public Map<String, String> getVariables() {
        return Map.copyOf(variableMap);
    }

    /**
     * Extracts and returns all symbols from an expression that are not yet assigned in memory.
     * (Only applies to variables for simplicity. For function symbols, custom extraction may be needed.)
     *
     * @param expression the expression to inspect
     * @return an array of unassigned variable or function symbols
     */
    public String[] getVariablesInExpression(String expression) {
        Matcher matcher = RegexValidator.matcher(expression, regexValidator.SYMBOL_PATTERN);
        List<String> variables = new ArrayList<>();
        while (matcher.find()) {
            String symbol = matcher.group(1);
            if (!variableMap.containsKey(symbol)) {
                variables.add(symbol);
            }
        }
        // Optionally, extend this to extract unassigned functions as well if required
        return variables.toArray(new String[0]);
    }
}