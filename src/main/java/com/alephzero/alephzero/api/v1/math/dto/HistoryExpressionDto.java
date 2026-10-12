package com.alephzero.alephzero.api.v1.math.dto;

import com.alephzero.alephzero.core.models.HistoryExpression;

import java.io.Serializable;

/**
 * Basic response DTO for {@link HistoryExpression}
 */
public record HistoryExpressionDto(
        Integer id,
        Integer indexOrder,
        MathExpressionResponseDto mathExpression) implements Serializable {
  }