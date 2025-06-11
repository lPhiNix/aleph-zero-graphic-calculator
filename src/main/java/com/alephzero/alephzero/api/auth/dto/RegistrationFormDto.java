package com.alephzero.alephzero.api.auth.dto;

import com.alephzero.alephzero.api.auth.validation.ValidRegisterForm;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

/**
 * DTO for user registration form.
 * <p>
 * This record encapsulates the data required for user registration, including username, email,
 * password, and confirmation of the password. It also applies validation constraints to ensure
 * that the fields are not blank and that the email is in a valid format.
 * <p>
 * The custom validation annotation {@link ValidRegisterForm} is used to enforce additional
 * business rules, such as ensuring that the username and email are unique.
 */
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
) implements Serializable {
}
