package com.alephzero.alephzero.api.v1.user.mapper;

import com.alephzero.alephzero.api.v1.user.dto.UserRoleDto;
import com.alephzero.alephzero.commons.mapper.BaseMapper;
import com.alephzero.alephzero.core.models.UserRole;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for {@link UserRole} ==> DTOs
 */
@Mapper(componentModel = "spring")
public interface RoleMapper extends BaseMapper<UserRole, UserRoleDto> {
}
