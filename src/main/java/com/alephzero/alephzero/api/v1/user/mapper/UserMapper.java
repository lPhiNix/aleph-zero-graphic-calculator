package com.alephzero.alephzero.api.v1.user.mapper;

import com.alephzero.alephzero.api.v1.user.dto.UserDto;
import com.alephzero.alephzero.core.models.User;
import com.alephzero.alephzero.commons.mapper.BaseMapper;
import com.alephzero.alephzero.core.models.UserRole;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for {@link User} ==> DTOs
 */
@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper extends BaseMapper<User, UserDto> {
}
