package com.placeholder.placeholder.db.mappers;

import com.placeholder.placeholder.api.math.dto.request.MathExpressionCreationDto;
import com.placeholder.placeholder.db.basicdto.MathExpressionDto;
import com.placeholder.placeholder.db.models.MathExpression;
import com.placeholder.placeholder.util.mapper.BaseMapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

public interface MathExpressionMapper extends BaseMapper<MathExpression, MathExpressionDto> {
    MathExpression toEntityFromCreationDto(MathExpressionCreationDto creationDto);

    @AfterMapping
    default void afterMapping(@MappingTarget MathExpressionCreationDto creationDto, MathExpression entity) {

    }
}
