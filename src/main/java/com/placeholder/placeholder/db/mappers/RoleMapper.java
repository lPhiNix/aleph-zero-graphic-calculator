package com.placeholder.placeholder.db.mappers;

import com.placeholder.placeholder.db.basicdto.UserRoleDto;
import com.placeholder.placeholder.api.util.common.mapper.BaseMapper;
import org.mapstruct.Mapper;

import javax.management.relation.Role;

@Mapper(componentModel = "string")
public interface RoleMapper extends BaseMapper<Role, UserRoleDto> {
}
