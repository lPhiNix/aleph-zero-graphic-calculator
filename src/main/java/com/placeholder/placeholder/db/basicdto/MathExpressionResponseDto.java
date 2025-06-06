package com.placeholder.placeholder.db.basicdto;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link com.placeholder.placeholder.db.models.MathExpression}
 */
public record MathExpressionResponseDto(
        Integer id,
        String expression,
        String points,
        Instant createdAt,
        Instant updatedAt,
        String snapshot
)
        implements Serializable {
}