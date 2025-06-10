package com.placeholder.placeholder.db.mappers;

import com.placeholder.placeholder.api.math.dto.request.history.UserHistoryCreationDto;
import com.placeholder.placeholder.db.basicdto.SimpleUserHistoryDto;
import com.placeholder.placeholder.api.math.service.persistence.SnapshotUtils;
import com.placeholder.placeholder.api.util.common.mapper.BaseMapper;
import com.placeholder.placeholder.api.util.common.mapper.MappingContext;
import com.placeholder.placeholder.api.util.common.mapper.MappingContextException;
import com.placeholder.placeholder.db.basicdto.UserHistoryDto;
import com.placeholder.placeholder.db.models.HistoryExpression;
import com.placeholder.placeholder.db.models.MathExpression;
import com.placeholder.placeholder.db.models.User;
import com.placeholder.placeholder.db.models.UserHistory;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {HistoryExpressionMapper.class, UserMapper.class, MathExpressionMapper.class})
public interface UserHistoryMapper extends BaseMapper<UserHistory, UserHistoryDto> {

    @Override
    @Mapping(source = "snapshot", target = "snapshot", qualifiedByName = "parseSnapshotToUrl")
    UserHistoryDto toResponseDtoFromEntity(UserHistory entity);

    @Mapping(source = "snapshot", target = "snapshot", qualifiedByName = "parseSnapshotToUrl")
    UserHistory toEntityFromCreationDto(UserHistoryCreationDto creationDto, @Context MappingContext context);

    @Mapping(source = "historyExpressions", target = "description", qualifiedByName = "getFirst")
    @Mapping(source = "snapshot", target = "snapshot", qualifiedByName = "parseSnapshotToUrl")
    SimpleUserHistoryDto toSimpleResponseDtoFromEntity(UserHistory entity);
    List<SimpleUserHistoryDto> toSimpleResponseDtoListFromEntityList(List<UserHistory> entities);

    @Named("getFirst")
    default String getFirst(Set<HistoryExpression> historyExpressions) {
        return historyExpressions.stream()
                .map(xpr -> xpr.getMathExpression().getExpression())
                .findFirst()
                .orElse(null);
    }

    @Named("parseSnapshotToUrl")
    default String parseSnapshotToUrl(String snapshot) {
        if (snapshot == null || snapshot.isEmpty()) {
            return null;
        }

        return SnapshotUtils.getSnapshotUrl(snapshot);
    }

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

        Set<HistoryExpression> historyExpressions = dto.mathExpressions().stream()
                .map(exprDto -> {
                    MathExpression expr = mathExpressionMapper.toEntityFromCreationDto(exprDto);
                    HistoryExpression hx = new HistoryExpression();
                    hx.setMathExpression(expr);
                    hx.setUserHistory(entity);
                    hx.setIndexOrder(exprDto.orderIndex());
                    return hx;
                })
                .collect(Collectors.toSet());

        entity.setHistoryExpressions(historyExpressions);
    }
}
