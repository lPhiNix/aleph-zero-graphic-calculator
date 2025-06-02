package com.placeholder.placeholder.api.auth.validation;

import com.placeholder.placeholder.api.auth.dto.RegistrationFormDto;
import com.placeholder.placeholder.api.user.service.UserService;
import jakarta.validation.ConstraintValidator;


import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor()
public class ValidRegisterFormValidator implements ConstraintValidator<ValidRegisterForm, RegistrationFormDto> {
    private final UserService userService;

    @Override
    public boolean isValid(RegistrationFormDto form, ConstraintValidatorContext context) {
        boolean valid = true;

        context.disableDefaultConstraintViolation();

        boolean usernameExists = userService.existsByUsername(form.username());
        boolean emailExists = userService.existsByEmail(form.email());
        boolean passwordsMatch = form.password().equals(form.confirmPassword());

        if (usernameExists) {
            context.buildConstraintViolationWithTemplate("Username already exists")
                    .addPropertyNode("username")
                    .addConstraintViolation();
            valid = false;
        }

        if (emailExists) {
            context.buildConstraintViolationWithTemplate("Email already registered")
                    .addPropertyNode("email")
                    .addConstraintViolation();
            valid = false;
        }

        if (!passwordsMatch) {
            context.buildConstraintViolationWithTemplate("Passwords do not match")
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation();
            valid = false;
        }

        return valid;
    }
}
