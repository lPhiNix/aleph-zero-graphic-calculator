package com.alephzero.alephzero.db.basicdto;

import java.io.Serializable;

/**
 * Basic response DTO for {@link com.alephzero.alephzero.db.models.UserRole}
 */
public record UserRoleDto(
        Integer id,
        String name,
        String description
) implements Serializable {
}