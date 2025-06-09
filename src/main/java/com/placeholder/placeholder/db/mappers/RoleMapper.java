package com.placeholder.placeholder.db.mappers;

import com.placeholder.placeholder.db.basicdto.UserRoleDto;
import com.placeholder.placeholder.api.util.common.mapper.BaseMapper;
import com.placeholder.placeholder.db.models.UserRole;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper extends BaseMapper<UserRole, UserRoleDto> {
}
