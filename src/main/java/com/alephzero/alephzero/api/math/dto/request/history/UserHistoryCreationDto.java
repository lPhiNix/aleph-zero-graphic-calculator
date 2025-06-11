package com.alephzero.alephzero.api.math.dto.request.history;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Data Transfer Object (DTO) for creating a user history entry.
 * <p>
 * This DTO encapsulates the necessary fields required to create a user history entry,
 * including the user ID, a hash of the image, and a list of mathematical expressions.
 * </p>
 */
public record UserHistoryCreationDto(
        @NotBlank String snapshot,
        @NotNull List<MathExpressionCreationDto> mathExpressions
) {}
