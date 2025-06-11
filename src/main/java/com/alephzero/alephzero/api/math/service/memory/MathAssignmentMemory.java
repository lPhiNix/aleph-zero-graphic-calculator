package com.alephzero.alephzero.api.math.service.memory;

import com.alephzero.alephzero.api.math.enums.validation.constants.MathConstants;
import com.alephzero.alephzero.api.math.regex.RegexValidator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@code MathAssignmentMemory} is a memory buffer component that manages assignment
 * of symbolic variables in mathematical expressions.
 * <p>
 * It allows storing and retrieving simple one-character variable definitions (e.g., {@code x=5}),
 * replacing these variables dynamically during expression evaluation. It also loads constant values
 * at initialization and validates symbol usage.
 * <p>
 * This service is especially useful for preserving user-defined variables across requests.
 */
@Component
public class MathAssignmentMemory {

    private static final Pattern ASSIGNMENT_PATTERN = Pattern.compile("^([a-z])=(.+)$"); // Variable assignment pattern (e.g., x=2)

    private final Map<String, String> variableMap = new HashMap<>(); // Stores variable assignments
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
     * Adds or replaces a variable assignment in memory.
     *
     * @param variable the variable symbol (e.g., "x")
     * @param value the value assigned to the variable (e.g., "3")
     * @return the previous value associated with the variable, or {@code null} if none
     */
    private String addVariable(String variable, String value) {
        return variableMap.put(variable, value);
    }

    /**
     * Processes a mathematical expression. If it is an assignment (e.g., "x=2"),
     * it stores the variable. Otherwise, it replaces known variables in the expression.
     *
     * @param expression the input expression
     * @return a confirmation of assignment or the updated expression with variables replaced
     */
    public String process(String expression) {
        Matcher matcher = ASSIGNMENT_PATTERN.matcher(expression.replaceAll("\\s+", ""));
        if (matcher.matches()) {
            String var = matcher.group(1);
            String value = matcher.group(2);
            addVariable(var, value);
            System.out.println(var + " = " + value);
            return var + " = " + value;
        }
        for (String s : variableMap.keySet()) {
            System.out.println(s + " + " + variableMap.get(s));
        }
        return replaceVariables(expression);
    }

    /**
     * Replaces all known variable symbols in the given expression with their assigned values.
     *
     * @param expression the original expression
     * @return the expression with variables replaced by their stored values
     */
    public String replaceVariables(String expression) {
        String result = expression;
        for (Map.Entry<String, String> entry : variableMap.entrySet()) {
            result = result.replaceAll("\\b" + Pattern.quote(entry.getKey()) + "\\b", entry.getValue());
        }
        return result;
    }

    /**
     * Clears all user-defined variables from memory.
     */
    public void clear() {
        variableMap.clear();
    }

    /**
     * Retrieves a copy of all current variable assignments.
     *
     * @return an unmodifiable map of variable-value pairs
     */
    public Map<String, String> getVariables() {
        return Map.copyOf(variableMap);
    }

    /**
     * Extracts and returns all symbols from an expression that are not yet assigned in memory.
     *
     * @param expression the expression to inspect
     * @return an array of unassigned variable symbols
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
        return variables.toArray(new String[0]);
    }
}
