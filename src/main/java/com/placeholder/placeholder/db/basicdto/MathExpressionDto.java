package com.placeholder.placeholder.db.basicdto;

import lombok.Value;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

/**
 * DTO for {@link com.placeholder.placeholder.db.models.MathExpression}
 */
@Value
public class MathExpressionDto implements Serializable {
    Integer id;
    String expression;
    Map<String, Object> pointsSnapshot;
    Instant createdAt;
    Instant updatedAt;
}