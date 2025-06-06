package com.placeholder.placeholder.db.mappers;

import com.placeholder.placeholder.api.math.dto.request.MathExpressionCreationDto;
import com.placeholder.placeholder.api.math.service.persistence.SnapshotUtils;
import com.placeholder.placeholder.db.basicdto.MathExpressionResponseDto;
import com.placeholder.placeholder.db.models.MathExpression;
import com.placeholder.placeholder.db.models.User;
import com.placeholder.placeholder.api.util.common.mapper.BaseMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MathExpressionMapper extends BaseMapper<MathExpression, MathExpressionResponseDto> {
    MathExpression toEntityFromCreationDto(MathExpressionCreationDto creationDto, @Context User context, @Context String imageHash);

    @AfterMapping
    default void afterMapping(@MappingTarget MathExpression entity, MathExpressionCreationDto creationDto, @Context User context, @Context String imageHash) {
        entity.setUser(context);
        entity.setSnapshot(imageHash);
    }

    @Mapping(target = "points", source = "points", qualifiedByName = "mapPointsIfIncluded")
    @Mapping(target = "snapshot", source = "snapshot", qualifiedByName = "parseSnapshotToUrl")
    MathExpressionResponseDto toResponseDtoFromEntity(MathExpression entity, @Context boolean includePoints);

    @Named("mapPointsIfIncluded")
    default String mapPointIfIncluded (String points, @Context boolean includePoints) {
        return includePoints ? points : null;
    }
    
    @Named("parseSnapshotToUrl")
    default String parseSnapshotToUrl (String snapshot) {
        return SnapshotUtils.getSnapshotUrl(snapshot);
    }
}
