package com.alephzero.alephzero.db.mappers;

import com.alephzero.alephzero.db.basicdto.UserRoleDto;
import com.alephzero.alephzero.api.util.common.mapper.BaseMapper;
import com.alephzero.alephzero.db.models.UserRole;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper extends BaseMapper<UserRole, UserRoleDto> {
}
