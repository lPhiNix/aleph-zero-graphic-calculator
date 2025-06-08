package com.placeholder.placeholder.api.math.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

/**
 * DTO for creating a math expression.
 * Contains the expression string, user identifier, and snapshot (image of the function).
 */
public record MathExpressionCreationDto(
        @NotBlank String expression,
        @NotBlank String points
)
        implements Serializable {
}
