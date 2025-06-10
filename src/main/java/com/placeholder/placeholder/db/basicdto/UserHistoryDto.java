package com.placeholder.placeholder.db.basicdto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

/**
 * DTO for {@link com.placeholder.placeholder.db.models.UserHistory}
 */
public record UserHistoryDto(
        Integer id,
        UserDto user,
        Instant createdAt,
        Instant updatedAt,
        String snapshot,

        @JsonProperty("expressions")
        Set<HistoryExpressionDto> historyExpressions) implements Serializable {
  }