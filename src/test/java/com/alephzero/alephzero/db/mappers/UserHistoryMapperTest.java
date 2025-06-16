package com.alephzero.alephzero.db.mappers;

import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.Assertions.assertThat;

import com.alephzero.alephzero.api.math.dto.request.history.MathExpressionCreationDto;
import com.alephzero.alephzero.api.math.dto.request.history.UserHistoryCreationDto;
import com.alephzero.alephzero.db.basicdto.UserHistoryDto;
import com.alephzero.alephzero.db.basicdto.HistoryExpressionDto;
import com.alephzero.alephzero.db.models.HistoryExpression;
import com.alephzero.alephzero.db.models.MathExpression;
import com.alephzero.alephzero.db.models.UserHistory;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Set;
import java.util.HashSet;

@SpringBootTest
class UserHistoryMapperTest {
    @Qualifier("userHistoryMapperImpl")
    @Autowired
    private UserHistoryMapper mapper;

    @Test
    void shouldMapEntityToDtoCorrectly() {
        // Prepare UserHistory entity
        UserHistory entity = new UserHistory();
        entity.setId(123);
        entity.setCreatedAt(Instant.parse("2025-01-01T10:00:00Z"));
        entity.setUpdatedAt(Instant.parse("2025-01-02T11:00:00Z"));

        // Prepare MathExpression and HistoryExpression
        MathExpression mathExpression = new MathExpression();
        mathExpression.setId(10);
        mathExpression.setExpression("2+2");
        mathExpression.setCalculation("4");

        HistoryExpression historyExpression = new HistoryExpression();
        historyExpression.setId(20);
        historyExpression.setMathExpression(mathExpression);
        historyExpression.setIndexOrder(1);
        historyExpression.setUserHistory(entity);

        // Assign expressions
        Set<HistoryExpression> exprs = new HashSet<>();
        exprs.add(historyExpression);
        entity.setHistoryExpressions(exprs);

        // Map to DTO
        UserHistoryDto dto = mapper.toResponseDtoFromEntity(entity);

        // Assertions
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(123);
        assertThat(dto.createdAt()).isEqualTo(Instant.parse("2025-01-01T10:00:00Z"));
        assertThat(dto.updatedAt()).isEqualTo(Instant.parse("2025-01-02T11:00:00Z"));
        assertThat(dto.historyExpressions()).hasSize(1);

        HistoryExpressionDto heDto = dto.historyExpressions().iterator().next();
        assertThat(heDto.id()).isEqualTo(20);
        assertThat(heDto.mathExpression().id()).isEqualTo(10);
        assertThat(heDto.indexOrder()).isEqualTo(1);
        assertThat(heDto.mathExpression().calculation()).isEqualTo("4");
    }
}
