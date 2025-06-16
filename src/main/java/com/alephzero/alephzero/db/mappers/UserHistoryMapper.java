package com.alephzero.alephzero.db.mappers;

import com.alephzero.alephzero.api.math.dto.request.history.UserHistoryCreationDto;
import com.alephzero.alephzero.db.basicdto.SimpleUserHistoryDto;
import com.alephzero.alephzero.api.math.service.persistence.SnapshotUtils;
import com.alephzero.alephzero.api.util.common.mapper.BaseMapper;
import com.alephzero.alephzero.api.util.common.mapper.MappingContext;
import com.alephzero.alephzero.api.util.common.mapper.MappingContextException;
import com.alephzero.alephzero.db.basicdto.UserHistoryDto;
import com.alephzero.alephzero.db.models.*;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for {@link UserHistory} ==> DTOs.
 */
@Mapper(componentModel = "spring", uses = {HistoryExpressionMapper.class, UserMapper.class, MathExpressionMapper.class})
public interface UserHistoryMapper extends BaseMapper<UserHistory, UserHistoryDto> {

    /**
     * Converts a {@link UserHistory} into a response {@link UserHistoryDto} with a transformed snapshot field.
     *
     * @param entity the {@link UserHistory} entity to convert
     * @return a mapped {@link UserHistoryDto}
     */
    @Override
    @Mapping(source = "snapshot", target = "snapshot", qualifiedByName = "parseSnapshotToUrl")
    UserHistoryDto toResponseDtoFromEntity(UserHistory entity);

    /**
     * Maps a {@link UserHistoryCreationDto} into a {@link UserHistory} entity.
     * The user, snapshot UUID, and expression mapper must be provided through the {@link MappingContext}.
     *
     * @param creationDto the incoming DTO for creation
     * @param context     mapping context containing auxiliary data
     * @return a mapped {@link UserHistory} entity
     */
    @Mapping(source = "snapshot", target = "snapshot", qualifiedByName = "parseSnapshotToUrl")
    UserHistory toEntityFromCreationDto(UserHistoryCreationDto creationDto, @Context MappingContext context);

    /**
     * Maps a {@link UserHistory} into a simplified version {@link SimpleUserHistoryDto},
     * including only the first math expression as the description.
     *
     * @param entity the entity to convert
     * @return a simplified DTO
     */
    @Mapping(source = "historyExpressions", target = "description", qualifiedByName = "getFirst")
    @Mapping(source = "snapshot", target = "snapshot", qualifiedByName = "parseSnapshotToUrl")
    SimpleUserHistoryDto toSimpleResponseDtoFromEntity(UserHistory entity);

    /**
     * Maps a list of {@link UserHistory} entities into a list of simplified DTOs.
     *
     * @param entities list of user history entities
     * @return list of simplified DTOs
     */
    List<SimpleUserHistoryDto> toSimpleResponseDtoListFromEntityList(List<UserHistory> entities);

    /**
     * Extracts the expression string from the first {@link HistoryExpression} in the set.
     *
     * @param historyExpressions a set of history expressions
     * @return the expression string of the first entry, or null if empty
     */
    @Named("getFirst")
    default String getFirst(Set<HistoryExpression> historyExpressions) {
        return historyExpressions.stream()
                .map(xpr -> xpr.getMathExpression().getExpression())
                .findFirst()
                .orElse(null);
    }

    /**
     * Transforms a snapshot UUID into a complete URL.
     *
     * @param snapshot the snapshot UUID
     * @return the full URL string, or null if input is null or empty
     */
    @Named("parseSnapshotToUrl")
    default String parseSnapshotToUrl(String snapshot) {
        if (snapshot == null || snapshot.isEmpty()) {
            return null;
        }
        return SnapshotUtils.getSnapshotUrl(snapshot);
    }

    /**
     * Performs additional setup after mapping a {@link UserHistory} from a creation DTO.
     * This method sets the user, snapshot UUID, and builds the {@link HistoryExpression} set
     * using the provided context.
     *
     * @param entity     the entity being populated
     * @param dto        the creation DTO
     * @param context    mapping context containing user, snapshot UUID, and a mapper
     */
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