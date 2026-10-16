package com.alephzero.alephzero.api.v1.math.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.alephzero.alephzero.api.v1.math.dto.request.history.MathExpressionCreationDto;
import com.alephzero.alephzero.api.v1.math.dto.request.history.UserHistoryCreationDto;
import com.alephzero.alephzero.api.v1.math.dto.UserHistoryDto;
import com.alephzero.alephzero.api.v1.math.dto.HistoryExpressionDto;
import com.alephzero.alephzero.api.v1.user.mapper.UserMapperImpl;
import com.alephzero.alephzero.core.models.HistoryExpression;
import com.alephzero.alephzero.core.models.MathExpression;
import com.alephzero.alephzero.core.models.MathExpressionPreferences;
import com.alephzero.alephzero.core.models.User;
import com.alephzero.alephzero.core.models.UserHistory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        UserHistoryMapperImpl.class,
        HistoryExpressionMapperImpl.class,
        MathExpressionMapperImpl.class,
        UserMapperImpl.class
})
class UserHistoryMapperTest {
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

    @Test
    void shouldMapCreationDtoToEntityCorrectly() {
        // Prepare creation DTO
        MathExpressionCreationDto expressionDto = new MathExpressionCreationDto(
                "2+2",
                1,
                "points",
                "evaluation",
                "4",
                new MathExpressionPreferences("#ffffff", "NUMERIC"));
        UserHistoryCreationDto creationDto = new UserHistoryCreationDto("raw-snapshot-content", List.of(expressionDto));

        // Prepare owner
        User owner = new User();
        owner.setId(7);
        owner.setUsername("neo");

        // Map to entity
        UserHistory entity = mapper.toEntityFromCreationDto(creationDto, owner, "uuid-123");

        // Assertions
        assertThat(entity.getUser()).isEqualTo(owner);
        assertThat(entity.getSnapshot()).isEqualTo("uuid-123");
        assertThat(entity.getHistoryExpressions()).hasSize(1);

        HistoryExpression historyExpression = entity.getHistoryExpressions().iterator().next();
        assertThat(historyExpression.getIndexOrder()).isEqualTo(1);
        assertThat(historyExpression.getMathExpression().getExpression()).isEqualTo("2+2");
        assertThat(historyExpression.getMathExpression().getCalculation()).isEqualTo("4");
        assertThat(historyExpression.getUserHistory()).isEqualTo(entity);
    }
}
