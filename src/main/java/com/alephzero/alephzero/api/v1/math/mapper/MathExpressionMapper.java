package com.alephzero.alephzero.api.v1.math.mapper;

import com.alephzero.alephzero.api.v1.math.dto.request.history.MathExpressionCreationDto;
import com.alephzero.alephzero.api.v1.math.dto.MathExpressionResponseDto;
import com.alephzero.alephzero.core.models.MathExpression;
import com.alephzero.alephzero.commons.mapper.BaseMapper;
import com.alephzero.alephzero.core.models.UserRole;
import org.mapstruct.*;


/**
 * MapStruct mapper for {@link MathExpression} ⇒ DTOs.
 * */
@Mapper(componentModel = "spring")
public interface MathExpressionMapper extends BaseMapper<MathExpression, MathExpressionResponseDto> {

    /**
     * Converts a {@link MathExpressionCreationDto} into a {@link MathExpression} entity.
     *
     * @param creationDto the DTO containing expression creation data
     * @return the mapped {@link MathExpression} entity
     */
    MathExpression toEntityFromCreationDto(MathExpressionCreationDto creationDto);

    /**
     * Converts a {@link MathExpression} entity into a {@link MathExpressionResponseDto},
     * with optional inclusion of points depending on the given context flag.
     *
     * @param entity        the {@link MathExpression} to map
     * @param includePoints whether the `points` attribute should be included in the response
     * @return the mapped {@link MathExpressionResponseDto}
     */
    @Mapping(target = "points", source = "points", qualifiedByName = "mapPointsIfIncluded")
    MathExpressionResponseDto toResponseDtoFromEntity(MathExpression entity, @Context boolean includePoints);

    /**
     * Conditionally includes the `points` field in the response based on the {@code includePoints} flag.
     *
     * @param points        the string representing the evaluated points
     * @param includePoints flag indicating whether to include the points or not
     * @return the original `points` if {@code includePoints} is true; otherwise {@code null}
     */
    @Named("mapPointsIfIncluded")
    default String mapPointIfIncluded(String points, @Context boolean includePoints) {
        return includePoints ? points : null;
    }
}

