package com.alephzero.alephzero.db.models;

/**
 * Model representing the preferences of a {@link MathExpression}
 * @param color color in hexadecimal.
 * @param xprType expression type.
 */
public record MathExpressionPreferences(
        String color,
        String xprType
) {}
