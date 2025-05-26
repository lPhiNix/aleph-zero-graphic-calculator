package com.placeholder.placeholder.api.auth.dto;

import com.placeholder.placeholder.api.util.common.messages.dto.content.MessageContent;

import java.io.Serializable;

public record UserRegisterRequestDto(
        String username,
        String password,
        String email
) implements MessageContent {
}
