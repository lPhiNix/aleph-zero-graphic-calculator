package com.placeholder.placeholder.api.math.enums;

import java.util.regex.Pattern;

public enum MathExpressionType {

    FUNCTION,   // It includes at least one variable (e.g., x^2 + sin(x))
    ASSIGNMENT, // variable = numeric expression (e.g., a = 1)
    EQUATION,   // expression == expression (e.g., x^2 + y^2 == 1)
    NUMERIC,    // integer, rational, decimal, or complex number without variables (e.g., 42, 3.14, 1/2, 5+3i)
    MATRIX,     // m x n matrix using nested braces (e.g., {{1, 2}, {3, 4}})
    VECTOR,     // One-dimensional vector using braces (e.g., {1, 2, 3})
    UNKNOWN,
    NONE;

    private static final Pattern FUNCTION_PATTERN = Pattern.compile("(?=.*[a-zA-Z])[-+*/^()a-zA-Z0-9\\s]+");
    private static final Pattern ASSIGNMENT_PATTERN = Pattern.compile("\\s*[a-zA-Z]\\s*=\\s*.+");
    private static final Pattern EQUATION_PATTERN = Pattern.compile(".+==.+");
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?([+-]\\d+i)?|-\\d+i|\\d+/\\d+");
    private static final Pattern MATRIX_PATTERN = Pattern.compile("\\{\\{\\s*-?\\d+(\\.\\d+)?(\\s*,\\s*-?\\d+(\\.\\d+)?)*\\s*}(,\\s*\\{\\s*-?\\d+(\\.\\d+)?(\\s*,\\s*-?\\d+(\\.\\d+)?)*\\s*})+}");
    private static final Pattern VECTOR_PATTERN = Pattern.compile("\\{\\s*-?\\d+(\\.\\d+)?(\\s*,\\s*-?\\d+(\\.\\d+)?)*\\s*}");

    public static MathExpressionType detectType(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            return MathExpressionType.NONE;
        }

        String trimmedExpr = expression.trim();
        if (EQUATION_PATTERN.matcher(trimmedExpr).matches()) {
            return MathExpressionType.EQUATION;
        } else if (ASSIGNMENT_PATTERN.matcher(trimmedExpr).matches()) {
            return MathExpressionType.ASSIGNMENT;
        } else if (MATRIX_PATTERN.matcher(trimmedExpr).matches()) {
            return MathExpressionType.MATRIX;
        } else if (VECTOR_PATTERN.matcher(trimmedExpr).matches()) {
            return MathExpressionType.VECTOR;
        } else if (NUMERIC_PATTERN.matcher(trimmedExpr).matches()) {
            return MathExpressionType.NUMERIC;
        } else if (FUNCTION_PATTERN.matcher(trimmedExpr).matches()) {
            return MathExpressionType.FUNCTION;
        }

        return MathExpressionType.UNKNOWN;
    }

    public static void main(String[] args) {
        System.out.println(MathExpressionType.detectType("1"));
    }
}