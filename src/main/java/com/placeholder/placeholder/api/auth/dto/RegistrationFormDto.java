package com.placeholder.placeholder.api.auth.dto;

import com.placeholder.placeholder.api.auth.validation.ValidRegisterForm;
import com.placeholder.placeholder.api.util.common.messages.dto.content.MessageContent;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@ValidRegisterForm // Custom validation annotation to check for unique username and email
public record RegistrationFormDto(

        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        String email,

        @NotBlank(message = "Password is required")
        String password,

        @NotBlank(message = "Confirm Password is required")
        String confirmPassword
) implements MessageContent {
}
