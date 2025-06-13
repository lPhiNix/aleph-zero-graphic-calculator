package com.alephzero.alephzero.api.auth.validation.validator;

import com.alephzero.alephzero.api.auth.validation.annotations.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Custom {@link jakarta.validation.ConstraintValidator} to validate user passwords.
 * This validator checks if a given password meets the following criteria:
 * <ul>
 * <li>Contains at least one uppercase letter.</li>
 * <li>Contains at least one lowercase letter.</li>
 * <li>Contains at least one digit.</li>
 * <li>Has a length between 8 and 255 characters (inclusive).</li>
 * </ul>
 */
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
    private static final String UPPERCASE_REGEX = ".*[A-Z].*";
    private static final String LOWERCASE_REGEX = ".*[a-z].*";
    private static final String DIGITS_REGEX = ".*\\d.*";

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 255;

    /**
     * Checks if the given password is valid according to the defined criteria.
     *
     * @param password The password to validate.
     * @param context  Context in which the constraint is evaluated.
     * @return {@code true} if the password is valid, {@code false} otherwise.
     */
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }

        boolean valid = true;

        // Clears previously added constraint violations to allow for multiple error messages.
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
            context.buildConstraintViolationWithTemplate("Password must be at least " + MIN_LENGTH + " characters long and less than " + MAX_LENGTH + ".")
                    .addConstraintViolation();
            valid = false;
        }

        return valid;
    }
}
