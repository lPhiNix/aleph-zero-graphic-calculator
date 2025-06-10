package com.placeholder.placeholder.api.math.dto.response;

import com.placeholder.placeholder.db.models.MathExpression;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

/**
 * DTO for {@link com.placeholder.placeholder.db.models.UserHistory}
 */
public record SimpleUserHistoryDto(
        Integer id,
        Instant createdAt,
        Instant updatedAt,
        String snapshot,
        String description) implements Serializable {
}