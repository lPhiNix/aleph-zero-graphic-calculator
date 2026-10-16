package com.alephzero.alephzero.api.v1.math.mapper;

import com.alephzero.alephzero.api.v1.math.dto.HistoryExpressionDto;
import com.alephzero.alephzero.api.v1.math.dto.request.history.MathExpressionCreationDto;
import com.alephzero.alephzero.core.models.HistoryExpression;
import com.alephzero.alephzero.infrastructure.config.GlobalMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for {@link HistoryExpression} ==> DTOs
 */
@Mapper(config = GlobalMapperConfig.class, uses = MathExpressionMapper.class)
public interface HistoryExpressionMapper {

    /**
     * Converts a {@link HistoryExpression} entity into a {@link HistoryExpressionDto}.
     *
     * @param entity the entity to convert
     * @return the converted DTO
     */
    HistoryExpressionDto toResponseDtoFromEntity(HistoryExpression entity);

    /**
     * Converts a {@link MathExpressionCreationDto} into a {@link HistoryExpression} entity.
     * <p>
     * The parent {@link com.alephzero.alephzero.core.models.UserHistory} is intentionally left unmapped:
     * it is a back-reference that the owning {@link UserHistoryMapper} links after mapping.
     *
     * @param dto the DTO containing the math expression creation data
     * @return the mapped {@link HistoryExpression} entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userHistory", ignore = true)
    @Mapping(target = "indexOrder", source = "orderIndex")
    @Mapping(target = "mathExpression", source = "dto")
    HistoryExpression toEntityFromCreationDto(MathExpressionCreationDto dto);
}
