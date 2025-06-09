package com.placeholder.placeholder.db.basicdto;

import com.placeholder.placeholder.api.math.dto.request.MathExpressionDto;

import java.io.Serializable;

/**
 * DTO for {@link com.placeholder.placeholder.db.models.HistoryExpression}
 */
public record HistoryExpressionDto(
        Integer id,
        MathExpressionDto mathExpression) implements Serializable {
  }