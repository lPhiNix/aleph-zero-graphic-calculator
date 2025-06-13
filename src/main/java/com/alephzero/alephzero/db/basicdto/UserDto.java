package com.alephzero.alephzero.db.basicdto;

import java.io.Serializable;


/**
 * Basic response DTO for {@link com.alephzero.alephzero.db.models.User}
 */
public record UserDto(
        String publicId,
        String username
) implements Serializable {
}