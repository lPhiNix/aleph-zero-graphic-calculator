package com.alephzero.alephzero.api.v1.math.mapper;

import com.alephzero.alephzero.commons.mapper.BaseMapper;
import com.alephzero.alephzero.api.v1.math.dto.HistoryExpressionDto;
import com.alephzero.alephzero.core.models.HistoryExpression;
import com.alephzero.alephzero.core.models.UserRole;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for {@link HistoryExpression} ==> DTOs
 */
@Mapper(componentModel = "spring", uses = MathExpressionMapper.class)
public interface HistoryExpressionMapper extends BaseMapper<HistoryExpression, HistoryExpressionDto> {
}
