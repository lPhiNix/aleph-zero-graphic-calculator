package com.alephzero.alephzero.db.basicdto;

import com.alephzero.alephzero.db.models.MathExpression;
import com.alephzero.alephzero.db.models.MathExpressionPreferences;

import java.io.Serializable;

/**
 * DTO for {@link MathExpression}
 */
public record MathExpressionResponseDto(
        Integer id,
        String expression,
        String points,
        MathExpressionPreferences preferences
)
        implements Serializable {
}