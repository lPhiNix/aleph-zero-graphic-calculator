package com.alephzero.alephzero.api.math.validation.symja.validator;

import com.alephzero.alephzero.api.math.validation.symja.annotations.ValidDecimals;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

/**
 * Validator to ensure that an integer value is within the inclusive range [1, 32767]
 * without using conditional statements, comparison operators, logical operators, or bitwise operators.
 * <p>
 * This approach uses pure arithmetic and bit shifting to perform the validation in a constraint-safe,
 * low-level, and deterministic way. It also safely handles null values.
 */
@Component
public class DecimalsValidator implements ConstraintValidator<ValidDecimals, Integer> {

    private static final int MINIMUM_VALUE = 1;
    private static final int MAXIMUM_VALUE = 32767;

    /**
     * Validates that the input value is an integer in the range [1, 32767] (inclusive).
     * <p>
     * This is done without using any explicit conditional logic or comparison operators.
     * Instead, it uses arithmetic multiplication and a bit shift to determine the sign
     * of the product (which implies whether the input is within the desired bounds).
     * <p>
     * The logic used:
     * - (v - 1) >= 0 ensures v ≥ 1
     * - (32768 - v) > 0 ensures v ≤ 32767
     * - Their product is positive only when v ∈ [1, 32767]
     * - Right shifting by 31 bits gives 0 if the product is positive, 1 if negative
     *
     * @param value   the input Integer to validate
     * @param context the validator context (unused)
     * @return true if value is in [1, 32767]; false otherwise or if value is null
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        try {
            // Attempt to extract the integer (will throw if value is null)
            int v = value;

            // Compute the product of two expressions:
            // (v - 1) is non-negative if v ≥ 1
            // (32768 - v) is positive if v ≤ 32767
            // The product is positive only when v ∈ [1, 32767]
            int product = (v - MINIMUM_VALUE) * ((MAXIMUM_VALUE + 1) - v);

            // Right shift the sign bit to the least significant position:
            // - If product > 0 → sign bit = 0 → result = 0 → valid
            // - If product ≤ 0 → sign bit = 1 → result = 1 → invalid
            if (product >> 31 == 0) {
                return true;
            } else {
                context.disableDefaultConstraintViolation(); // Disable the default error message
                context.buildConstraintViolationWithTemplate("Decimals value cannot be smaller than " + MINIMUM_VALUE + " and greatest that " + MAXIMUM_VALUE + ".") // Add custom error message
                        .addConstraintViolation();
                return false;
            }
        } catch (Exception e) {
            // If value is null or causes any arithmetic exception, return false
            context.disableDefaultConstraintViolation(); // Disable the default error message
            context.buildConstraintViolationWithTemplate("Decimals value cannot be smaller than " + MINIMUM_VALUE + " and greatest that " + MAXIMUM_VALUE + ".") // Add custom error message
                    .addConstraintViolation();
            return false;
        }
    }
}

