package com.alephzero.alephzero.api.v1.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Login request body.
 *
 * @param username   the username <b>or</b> email of the user
 * @param password   the raw password
 * @param rememberMe whether the refresh cookie should be persistent (survive browser restarts)
 */
public record LoginRequest(

        @NotBlank(message = "Username or email is required")
        String username,

        @NotBlank(message = "Password is required")
        String password,

        boolean rememberMe
) {
}
