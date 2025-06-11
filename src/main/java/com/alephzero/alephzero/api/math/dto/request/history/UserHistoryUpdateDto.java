package com.alephzero.alephzero.api.math.dto.request.history;

import com.alephzero.alephzero.db.basicdto.HistoryExpressionDto;
import com.alephzero.alephzero.db.models.UserHistory;
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