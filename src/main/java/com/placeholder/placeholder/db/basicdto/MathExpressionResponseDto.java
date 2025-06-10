package com.placeholder.placeholder.db.basicdto;

import com.placeholder.placeholder.db.models.MathExpressionPreferences;

import java.io.Serializable;

/**
 * DTO for {@link com.placeholder.placeholder.db.models.MathExpression}
 */
public record MathExpressionResponseDto(
        Integer id,
        String expression,
        String points,
        MathExpressionPreferences preferences
)
        implements Serializable {
}