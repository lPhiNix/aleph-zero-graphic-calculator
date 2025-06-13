package com.alephzero.alephzero.api.auth.validation.validator;

import com.alephzero.alephzero.api.auth.dto.RegistrationFormDto;
import com.alephzero.alephzero.api.auth.validation.annotations.ValidRegisterForm;
import com.alephzero.alephzero.api.user.service.UserService;
import jakarta.validation.ConstraintValidator;


import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

/**
 * Validator for the {@link RegistrationFormDto} to ensure the registration form data is valid.
 * Implements the {@link ConstraintValidator} interface to provide custom validation logic.
 */
@RequiredArgsConstructor()
public class ValidRegisterFormValidator implements ConstraintValidator<ValidRegisterForm, RegistrationFormDto> {

    /**
     * Service for user-related operations, used to check if a username or email already exists.
     */
    private final UserService userService;

    /**
     * Validates the `RegistrationFormDto` object.
     *
     * @param form the registration form data to validate
     * @param context the context in which the constraint is evaluated
     * @return `true` if the form is valid, `false` otherwise
     */
    @Override
    public boolean isValid(RegistrationFormDto form, ConstraintValidatorContext context) {
        boolean valid = true;

        // Disable default constraint violation messages
        context.disableDefaultConstraintViolation();

        // Check if the username already exists
        boolean usernameExists = userService.existsByUsername(form.username());
        // Check if the email is already registered
        boolean emailExists = userService.existsByEmail(form.email());
        // Check if the password and confirm password fields match
        boolean passwordsMatch = form.password().equals(form.confirmPassword());

        // Add a constraint violation if the username already exists
        if (usernameExists) {
            context.buildConstraintViolationWithTemplate("Username already exists")
                    .addPropertyNode("username")
                    .addConstraintViolation();
            valid = false;
        }

        // Add a constraint violation if the email is already registered
        if (emailExists) {
            context.buildConstraintViolationWithTemplate("Email already registered")
                    .addPropertyNode("email")
                    .addConstraintViolation();
            valid = false;
        }

        // Add a constraint violation if the passwords do not match
        if (!passwordsMatch) {
            context.buildConstraintViolationWithTemplate("Passwords do not match")
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation();
            valid = false;
        }

        return valid;
    }
}