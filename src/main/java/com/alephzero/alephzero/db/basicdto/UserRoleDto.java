package com.alephzero.alephzero.db.basicdto;

import java.io.Serializable;


public record UserRoleDto(
        Integer id,
        String name,
        String description
) implements Serializable {
}