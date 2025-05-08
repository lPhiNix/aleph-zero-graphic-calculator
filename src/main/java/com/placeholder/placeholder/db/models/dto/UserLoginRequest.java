package com.placeholder.placeholder.db.models.dto;

import com.placeholder.placeholder.util.validation.conditions.ValidPassword;
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
