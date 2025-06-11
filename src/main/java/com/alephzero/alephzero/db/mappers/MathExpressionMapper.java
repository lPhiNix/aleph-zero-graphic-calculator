package com.alephzero.alephzero.db.mappers;

import com.alephzero.alephzero.api.math.dto.request.history.MathExpressionCreationDto;
import com.alephzero.alephzero.db.basicdto.MathExpressionResponseDto;
import com.alephzero.alephzero.db.models.MathExpression;
import com.alephzero.alephzero.api.util.common.mapper.BaseMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MathExpressionMapper extends BaseMapper<MathExpression, MathExpressionResponseDto> {

    MathExpression toEntityFromCreationDto(MathExpressionCreationDto creationDto);

    @Mapping(target = "points", source = "points", qualifiedByName = "mapPointsIfIncluded")
    MathExpressionResponseDto toResponseDtoFromEntity(MathExpression entity, @Context boolean includePoints);

    @Named("mapPointsIfIncluded")
    default String mapPointIfIncluded (String points, @Context boolean includePoints) {
        return includePoints ? points : null;
    }
}
