package com.placeholder.placeholder.db.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents preferences for a mathematical expression.
 * <p>
 * This class encapsulates various settings that can be applied to a mathematical expression,
 * such as color and type of expression.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
public class MathExpressionPreferences {
    private String color;
    private String xprType;
    // more to add.
}
