package com.placeholder.placeholder.db.mappers;

import com.placeholder.placeholder.api.math.dto.request.UserHistoryCreationDto;
import com.placeholder.placeholder.api.util.common.mapper.BaseMapper;
import com.placeholder.placeholder.api.util.common.mapper.MappingContext;
import com.placeholder.placeholder.api.util.common.mapper.MappingContextException;
import com.placeholder.placeholder.db.basicdto.UserHistoryDto;
import com.placeholder.placeholder.db.models.HistoryExpression;
import com.placeholder.placeholder.db.models.MathExpression;
import com.placeholder.placeholder.db.models.User;
import com.placeholder.placeholder.db.models.UserHistory;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {HistoryExpressionMapper.class, UserMapper.class, MathExpressionMapper.class})
public interface UserHistoryMapper extends BaseMapper<UserHistory, UserHistoryDto> {
    UserHistory toEntityFromCreationDto(UserHistoryCreationDto creationDto, @Context MappingContext context);

    @AfterMapping
    default void afterMappingFromCreation(@MappingTarget UserHistory entity, UserHistoryCreationDto dto, @Context MappingContext context) {
        User user = context.getContextData(User.class)
                        .orElseThrow(() -> new MappingContextException("User not found in context for UserHistory creation"));
        String snapshotUuid = context.getContextData(String.class)
                        .orElseThrow(() -> new MappingContextException("Snapshot UUID not found in context for UserHistory creation"));
        MathExpressionMapper mathExpressionMapper = context.getContextData(MathExpressionMapper.class)
                        .orElseThrow(() -> new MappingContextException("MathExpressionMapper not found in context for UserHistory creation"));

        entity.setUser(user);
        entity.setSnapshot(snapshotUuid);

        Set<MathExpression> expressions = dto.mathExpressions().stream()
                .map(mathExpressionMapper::toEntityFromCreationDto)
                .collect(Collectors.toSet());

        Set<HistoryExpression> historyExpressions = expressions.stream()
                .map(expr -> {
                    HistoryExpression hx = new HistoryExpression();
                    hx.setMathExpression(expr);
                    hx.setUserHistory(entity);
                    return hx;
                })
                .collect(Collectors.toSet());

        entity.setHistoryExpressions(historyExpressions);
    }
}
