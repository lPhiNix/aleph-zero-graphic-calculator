package com.placeholder.placeholder.db.basicdto;
import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link com.placeholder.placeholder.db.models.UserHistory}
 */
public record UserHistoryDto(
        Integer id,
        UserDto user,
        Instant createdAt,
        Instant updatedAt,
        String snapshot) implements Serializable {
  }