package com.placeholder.placeholder.db.mappers;

import com.placeholder.placeholder.api.math.dto.request.MathExpressionCreationDto;
import com.placeholder.placeholder.db.basicdto.MathExpressionDto;
import com.placeholder.placeholder.db.models.MathExpression;
import com.placeholder.placeholder.db.models.User;
import com.placeholder.placeholder.api.util.common.mapper.BaseMapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.MappingTarget;

public interface MathExpressionMapper extends BaseMapper<MathExpression, MathExpressionDto> {
    MathExpression toEntityFromCreationDto(MathExpressionCreationDto creationDto, @Context User context);

    @AfterMapping
    default void afterMapping(@MappingTarget MathExpressionCreationDto creationDto, MathExpression entity, @Context User context) {
        entity.setUser(context);
    }
}
