package com.alephzero.alephzero.api.v1.user.dto;

import java.io.Serializable;

/**
 * Basic response DTO for {@link com.alephzero.alephzero.core.models.UserRole}
 */
public record UserRoleDto(
        Integer id,
        String name,
        String description
) implements Serializable {
}