package com.alephzero.alephzero.api.v1.math.dto;

import com.alephzero.alephzero.core.models.UserHistory;

import java.io.Serializable;
import java.time.Instant;

/**
 * Simplified response DTO for {@link UserHistory}
 */
public record SimpleUserHistoryDto(
        Integer id,
        Instant createdAt,
        Instant updatedAt,
        String snapshot,
        String description) implements Serializable {
}