package com.alephzero.alephzero.db.mappers;

import com.alephzero.alephzero.api.util.common.mapper.BaseMapper;
import com.alephzero.alephzero.db.basicdto.HistoryExpressionDto;
import com.alephzero.alephzero.db.models.HistoryExpression;
import com.alephzero.alephzero.db.models.UserRole;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for {@link HistoryExpression} ==> DTOs
 */
@Mapper(componentModel = "spring", uses = MathExpressionMapper.class)
public interface HistoryExpressionMapper extends BaseMapper<HistoryExpression, HistoryExpressionDto> {
}
