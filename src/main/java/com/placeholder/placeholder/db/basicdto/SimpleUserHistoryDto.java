package com.placeholder.placeholder.db.basicdto;

import java.io.Serializable;
import java.time.Instant;

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