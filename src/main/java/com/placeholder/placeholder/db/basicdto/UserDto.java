package com.placeholder.placeholder.db.basicdto;

import java.io.Serializable;


public record UserDto(
        String publicId,
        String username
) implements Serializable {
}