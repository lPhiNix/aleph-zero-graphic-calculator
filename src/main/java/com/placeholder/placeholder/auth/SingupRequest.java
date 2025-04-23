package com.placeholder.placeholder.auth;

public record SingupRequest(
        String username,
        String email,
        String password,
        Integer roleId) {}

