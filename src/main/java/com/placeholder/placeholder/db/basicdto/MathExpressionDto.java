package com.placeholder.placeholder.db.basicdto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

/**
 * DTO for {@link com.placeholder.placeholder.db.models.MathExpression}
 */
public record MathExpressionDto(
        Integer id, String expression,
        Map<String, Object> pointsSnapshot,
        Instant createdAt,
        Instant updatedAt
) implements Serializable {
}