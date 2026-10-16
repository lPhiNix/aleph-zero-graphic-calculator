package com.alephzero.alephzero.api.v1.math.mapper;

import com.alephzero.alephzero.api.v1.math.dto.request.history.MathExpressionCreationDto;
import com.alephzero.alephzero.api.v1.math.dto.MathExpressionResponseDto;
import com.alephzero.alephzero.core.models.MathExpression;
import com.alephzero.alephzero.infrastructure.config.GlobalMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


/**
 * MapStruct mapper for {@link MathExpression} ⇒ DTOs.
 * */
@Mapper(config = GlobalMapperConfig.class)
public interface MathExpressionMapper {

    /**
     * Converts a {@link MathExpression} entity into a {@link MathExpressionResponseDto}.
     *
     * @param entity the entity to convert
     * @return the converted DTO
     */
    MathExpressionResponseDto toResponseDtoFromEntity(MathExpression entity);

    /**
     * Converts a {@link MathExpressionCreationDto} into a {@link MathExpression} entity.
     *
     * @param creationDto the DTO containing expression creation data
     * @return the mapped {@link MathExpression} entity
     */
    @Mapping(target = "id", ignore = true)
    MathExpression toEntityFromCreationDto(MathExpressionCreationDto creationDto);
}
