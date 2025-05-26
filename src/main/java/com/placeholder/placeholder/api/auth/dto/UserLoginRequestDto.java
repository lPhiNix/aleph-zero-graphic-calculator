package com.placeholder.placeholder.api.auth.dto;

public record UserLoginRequestDto(
        String identifier,
        String password
) {
}
