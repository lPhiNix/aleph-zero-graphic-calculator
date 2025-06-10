package com.placeholder.placeholder.db.mappers;

import com.placeholder.placeholder.api.util.common.mapper.BaseMapper;
import com.placeholder.placeholder.db.basicdto.HistoryExpressionDto;
import com.placeholder.placeholder.db.models.HistoryExpression;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = MathExpressionMapper.class)
public interface HistoryExpressionMapper extends BaseMapper<HistoryExpression, HistoryExpressionDto> {
}
