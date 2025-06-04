package com.placeholder.placeholder.db.mappers;

import com.placeholder.placeholder.db.basicdto.UserDto;
import com.placeholder.placeholder.db.models.User;
import com.placeholder.placeholder.util.mapper.BaseMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper extends BaseMapper<User, UserDto> {
}
