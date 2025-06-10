package com.placeholder.placeholder.db.basicdto;

import java.io.Serializable;

/**
 * DTO for {@link com.placeholder.placeholder.db.models.HistoryExpression}
 */
public record HistoryExpressionDto(
        Integer id,
        MathExpressionResponseDto mathExpression) implements Serializable {
  }