package com.placeholder.placeholder.api.math.service.memory;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MathAssignmentMemory {

    private final Map<String, String> variableMap = new HashMap<>();

    private static final Pattern ASSIGNMENT_PATTERN = Pattern.compile("^([a-z])=(.+)$");

    public String process(String expression) {
        Matcher matcher = ASSIGNMENT_PATTERN.matcher(expression.replaceAll("\\s+", ""));
        if (matcher.matches()) {
            String var = matcher.group(1);
            String value = matcher.group(2);
            variableMap.put(var, value);
            return var + " = " + value;
        }
        return replaceVariables(expression);
    }

    public String replaceVariables(String expression) {
        String result = expression;
        for (Map.Entry<String, String> entry : variableMap.entrySet()) {
            result = result.replaceAll("\\b" + Pattern.quote(entry.getKey()) + "\\b", entry.getValue());
        }
        return result;
    }

    public void clear() {
        variableMap.clear();
    }

    public Map<String, String> getVariables() {
        return Map.copyOf(variableMap);
    }
}
