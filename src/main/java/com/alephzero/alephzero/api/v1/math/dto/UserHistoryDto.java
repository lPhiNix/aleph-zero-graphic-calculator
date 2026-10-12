package com.alephzero.alephzero.api.v1.math.dto;
import com.alephzero.alephzero.api.v1.user.dto.UserDto;
import com.alephzero.alephzero.core.models.UserHistory;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

/**
 * Basic response DTO for {@link UserHistory}
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