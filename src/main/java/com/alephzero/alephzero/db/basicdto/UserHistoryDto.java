package com.alephzero.alephzero.db.basicdto;
import com.alephzero.alephzero.db.models.UserHistory;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

/**
 * DTO for {@link UserHistory}
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