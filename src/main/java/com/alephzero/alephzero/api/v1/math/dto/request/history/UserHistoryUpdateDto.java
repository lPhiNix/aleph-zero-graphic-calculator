package com.alephzero.alephzero.api.v1.math.dto.request.history;

import com.alephzero.alephzero.api.v1.math.dto.HistoryExpressionDto;
import com.alephzero.alephzero.core.models.UserHistory;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link UserHistory}
 */
public record UserHistoryUpdateDto(
        @NotNull(message = "-")
        Integer id,
        @Size(max = 36)
        String snapshot,
        Set<HistoryExpressionDto> historyExpressions
) implements Serializable {

}