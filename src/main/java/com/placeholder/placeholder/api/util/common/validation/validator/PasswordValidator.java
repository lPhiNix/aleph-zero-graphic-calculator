package com.placeholder.placeholder.api.util.common.validation.validator;

import com.placeholder.placeholder.api.util.common.validation.conditions.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Custom {@link ConstraintValidator} to validate user passwords
 */
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
    private static final String UPPERCASE_REGEX = ".*[A-Z].*";
    private static final String LOWERCASE_REGEX = ".*[a-z].*";
    private static final String DIGITS_REGEX = ".*\\d.*";

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 255;

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }

        boolean valid = true;

        //Clears previous messages
        context.disableDefaultConstraintViolation();

        if (!password.matches(UPPERCASE_REGEX)) {
            context.buildConstraintViolationWithTemplate("Password must contain at least one uppercase letter.")
                    .addConstraintViolation();
            valid = false;
        }

        if (!password.matches(LOWERCASE_REGEX)) {
            context.buildConstraintViolationWithTemplate("Password must contain at least one lowercase letter.")
                    .addConstraintViolation();
            valid = false;
        }

        if (!password.matches(DIGITS_REGEX)) {
            context.buildConstraintViolationWithTemplate("Password must contain at least one number.")
                    .addConstraintViolation();
            valid = false;
        }

        if (password.length() < MIN_LENGTH || password.length() > MAX_LENGTH) {
            context.buildConstraintViolationWithTemplate("Password must be at least 8 characters long and less than 255.")
                    .addConstraintViolation();
            valid = false;
        }

        return valid;
    }
}
