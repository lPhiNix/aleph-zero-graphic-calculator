package com.alephzero.alephzero.api.v1.user.dto;

import java.io.Serializable;


/**
 * Basic response DTO for {@link com.alephzero.alephzero.core.models.User}
 */
public record UserDto(
        String publicId,
        String username
) implements Serializable {
}