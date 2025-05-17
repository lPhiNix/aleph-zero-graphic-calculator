package com.placeholder.placeholder.api.math.services;

public enum MathExpressionType {
    FUNCTION,   // It includes at least one variable (e.g., x^2 + sin(x))
    ASSIGNMENT, // variable = numeric expression (e.g., a = 1)
    EQUATION,   // expression == expression (e.g., x^2 + y^2 == 1)
    NUMERIC,    // integer, rational, decimal, or complex number without variables (e.g., 42, 3.14, 1/2, 5+3i)
    MATRIX,     // m x n matrix using nested braces (e.g., {{1, 2}, {3, 4}})
    VECTOR,     // One-dimensional vector using braces (e.g., {1, 2, 3})
    NONE
}
