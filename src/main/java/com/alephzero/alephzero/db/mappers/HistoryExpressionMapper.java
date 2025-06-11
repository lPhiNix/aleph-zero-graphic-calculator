package com.alephzero.alephzero.db.mappers;

import com.alephzero.alephzero.api.util.common.mapper.BaseMapper;
import com.alephzero.alephzero.db.basicdto.HistoryExpressionDto;
import com.alephzero.alephzero.db.models.HistoryExpression;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = MathExpressionMapper.class)
public interface HistoryExpressionMapper extends BaseMapper<HistoryExpression, HistoryExpressionDto> {
}
