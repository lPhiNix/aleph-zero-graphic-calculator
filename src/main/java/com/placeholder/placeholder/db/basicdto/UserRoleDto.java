package com.placeholder.placeholder.db.basicdto;

import java.io.Serializable;


public record UserRoleDto(
        Integer id,
        String name,
        String description
) implements Serializable {
}