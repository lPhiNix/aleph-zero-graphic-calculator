package com.alephzero.alephzero.api.v1.user.mapper;

import com.alephzero.alephzero.api.v1.user.dto.UserRoleDto;
import com.alephzero.alephzero.core.models.UserRole;
import com.alephzero.alephzero.infrastructure.config.GlobalMapperConfig;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for {@link UserRole} ==> DTOs
 */
@Mapper(config = GlobalMapperConfig.class)
public interface RoleMapper {

    /**
     * Converts a {@link UserRole} entity into a {@link UserRoleDto}.
     *
     * @param entity the entity to convert
     * @return the converted DTO
     */
    UserRoleDto toResponseDtoFromEntity(UserRole entity);
}
