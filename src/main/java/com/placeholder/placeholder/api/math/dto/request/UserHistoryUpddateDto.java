package com.placeholder.placeholder.api.math.dto.request;

import com.placeholder.placeholder.db.basicdto.HistoryExpressionDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link com.placeholder.placeholder.db.models.UserHistory}
 */
public record UserHistoryUpddateDto(
        @NotNull(message = "-")
        Integer id,
        @Size(max = 36)
        String snapshot,
        Set<HistoryExpressionDto> historyExpressions
) implements Serializable {

}