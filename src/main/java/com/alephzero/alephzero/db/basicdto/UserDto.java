package com.alephzero.alephzero.db.basicdto;

import java.io.Serializable;


public record UserDto(
        String publicId,
        String username
) implements Serializable {
}