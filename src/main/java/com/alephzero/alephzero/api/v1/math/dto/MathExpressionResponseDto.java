package com.alephzero.alephzero.api.v1.math.dto;

import com.alephzero.alephzero.core.models.MathExpression;
import com.alephzero.alephzero.core.models.MathExpressionPreferences;

import java.io.Serializable;

/**
 * Basic response DTO for {@link MathExpression}
 */
public record MathExpressionResponseDto(
        Integer id,
        String expression,
        String points,
        String evaluation,
        String calculation,
        MathExpressionPreferences preferences
)
        implements Serializable {
}