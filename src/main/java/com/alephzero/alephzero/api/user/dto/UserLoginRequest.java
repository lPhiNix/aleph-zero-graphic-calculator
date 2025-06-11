package com.alephzero.alephzero.api.user.dto;

import com.alephzero.alephzero.api.user.validation.annotations.ValidPassword;
import jakarta.validation.constraints.Email;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

public record UserLoginRequest(
        //Only one of these
        String username,
        @Email
        String email,

        @ValidPassword
        @NotBlank
        String password
) {}
