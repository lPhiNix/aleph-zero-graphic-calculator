package com.placeholder.placeholder.db.basicdto;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link com.placeholder.placeholder.db.models.MathExpression}
 */
public record MathExpressionDto(
        Integer id,
        String expression,
        Instant createdAt,
        Instant updatedAt,
        String snapshot
)
        implements Serializable {
}