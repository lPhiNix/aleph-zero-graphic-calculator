package com.placeholder.placeholder.db.basicdto;

import java.io.Serializable;


public record UserRoleDto(
        String name,
        String description
) implements Serializable {
}