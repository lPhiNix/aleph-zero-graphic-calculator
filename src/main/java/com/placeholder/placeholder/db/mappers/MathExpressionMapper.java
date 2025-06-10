package com.placeholder.placeholder.db.mappers;

import com.placeholder.placeholder.api.math.dto.request.history.MathExpressionCreationDto;
import com.placeholder.placeholder.db.basicdto.MathExpressionResponseDto;
import com.placeholder.placeholder.db.models.MathExpression;
import com.placeholder.placeholder.api.util.common.mapper.BaseMapper;
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
