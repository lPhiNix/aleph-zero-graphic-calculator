package com.alephzero.alephzero.db.basicdto;

import com.alephzero.alephzero.db.models.UserHistory;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link UserHistory}
 */
public record SimpleUserHistoryDto(
        Integer id,
        Instant createdAt,
        Instant updatedAt,
        String snapshot,
        String description) implements Serializable {
}