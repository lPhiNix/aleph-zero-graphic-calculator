package com.placeholder.placeholder.db.basicdto;

import java.io.Serializable;


public record UserDto(
        String username,
        String email,
        UserRoleDto role
) implements Serializable {
}