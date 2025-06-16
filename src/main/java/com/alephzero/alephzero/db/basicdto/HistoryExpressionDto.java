package com.alephzero.alephzero.db.basicdto;

import com.alephzero.alephzero.db.models.HistoryExpression;

import java.io.Serializable;

/**
 * Basic response DTO for {@link HistoryExpression}
 */
public record HistoryExpressionDto(
        Integer id,
        Integer indexOrder,
        MathExpressionResponseDto mathExpression) implements Serializable {
  }