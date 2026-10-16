package com.alephzero.alephzero.api.v1.user.mapper;

import com.alephzero.alephzero.api.v1.user.dto.UserDto;
import com.alephzero.alephzero.core.models.User;
import com.alephzero.alephzero.infrastructure.config.GlobalMapperConfig;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for {@link User} ==> DTOs
 */
@Mapper(config = GlobalMapperConfig.class)
public interface UserMapper {

    /**
     * Converts a {@link User} entity into a {@link UserDto}.
     *
     * @param entity the entity to convert
     * @return the converted DTO
     */
    UserDto toResponseDtoFromEntity(User entity);
}
