package com.placeholder.placeholder.api.math.enums.computation;

/**
 * Enum {@code MathExpressionType} categorizes types of mathematical expressions.
 * <p>
 * It includes function expressions, assignments, equations, numeric literals,
 * matrices, vectors, boolean expressions, unknown types, and a none type.
 * </p>
 * <ul>
 *     <li><b>FUNCTION</b>: Expressions containing at least one variable (e.g., x^2 + sin(x))</li>
 *     <li><b>ASSIGNMENT</b>: Variable assignment to a numeric expression (e.g., a = 1)</li>
 *     <li><b>EQUATION</b>: Equality expressions between two expressions (e.g., x^2 + y^2 == 1)</li>
 *     <li><b>NUMERIC</b>: Numeric literals without variables (e.g., 42, 3.14, 1/2, 5+3i)</li>
 *     <li><b>MATRIX</b>: Matrix expressions represented by nested braces (e.g., {{1, 2}, {3, 4}})</li>
 *     <li><b>VECTOR</b>: One-dimensional vectors represented by braces (e.g., {1, 2, 3})</li>
 *     <li><b>BOOLEAN</b>: Boolean numeric comparisons (e.g., 2 = 1)</li>
 *     <li><b>UNKNOWN</b>: Expressions that do not fit known categories</li>
 *     <li><b>NONE</b>: No expression type</li>
 * </ul>
 */
public enum MathExpressionType {
    FUNCTION,   // It includes at least one variable (e.g., x^2 + sin(x))
    ASSIGNMENT, // variable = numeric expression (e.g., a = 1)
    EQUATION,   // expression == expression (e.g., x^2 + y^2 == 1)
    NUMERIC,    // integer, rational, decimal, or complex number without variables (e.g., 42, 3.14, 1/2, 5+3i)
    MATRIX,     // m x n matrix using nested braces (e.g., {{1, 2}, {3, 4}})
    VECTOR,     // One-dimensional vector using braces (e.g., {1, 2, 3})
    BOOLEAN,    // numeric = numeric expression (e.g 2 = 1)
    UNKNOWN,
    NONE
}
