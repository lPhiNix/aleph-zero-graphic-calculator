package com.placeholder.placeholder.db.basicdto;

import com.placeholder.placeholder.api.util.common.messages.dto.content.MessageContent;

import java.io.Serializable;


public record UserDto(
        String publicId,
        String username,
        String email,
        UserRoleDto role
) implements MessageContent {
}