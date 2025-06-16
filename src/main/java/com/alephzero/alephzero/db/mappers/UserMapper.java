package com.alephzero.alephzero.db.mappers;

import com.alephzero.alephzero.db.basicdto.UserDto;
import com.alephzero.alephzero.db.models.User;
import com.alephzero.alephzero.api.util.common.mapper.BaseMapper;
import com.alephzero.alephzero.db.models.UserRole;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for {@link User} ==> DTOs
 */
@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper extends BaseMapper<User, UserDto> {
}
