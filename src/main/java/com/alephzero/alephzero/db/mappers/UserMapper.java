package com.alephzero.alephzero.db.mappers;

import com.alephzero.alephzero.db.basicdto.UserDto;
import com.alephzero.alephzero.db.models.User;
import com.alephzero.alephzero.api.util.common.mapper.BaseMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper extends BaseMapper<User, UserDto> {
}
