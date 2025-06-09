package com.placeholder.placeholder.api.math.dto.request;

import com.placeholder.placeholder.db.models.MathExpressionPreferences;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * Data Transfer Object (DTO) for creating a new mathematical expression.
 * <p>
 * This DTO encapsulates the necessary fields required to create a mathematical expression,
 * including the expression itself, points associated with it, and preferences for its evaluation.
 * </p>
 */
public record MathExpressionCreationDto(
        @NotBlank String expression,
        @NotBlank String points,
        @NotNull MathExpressionPreferences preferences
)
        implements Serializable {
}
