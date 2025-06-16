package com.alephzero.alephzero.api.auth.validation.annotations;

import com.alephzero.alephzero.api.auth.validation.validator.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom Jakarta Bean Validation annotation used to validate if a string value
 * represents a valid user password according to defined rules.
 * <p>
 * This annotation is processed by the associated {@link PasswordValidator} class, which enforces rules such as:
 * <ul>
 *     <li>TODO: VALID PASSWORD CONDITION LIST</li>
 * </ul>
 * </p>
 */
@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {

    /**
     * Defines the default error message that will be generated if the validation performed by
     * {@link PasswordValidator} fails.
     * This message informs the user that the password does not meet the required criteria.
     *
     * @return The error message indicating that the password is not valid.
     */
    String message() default "Password is not valid. Please meet all the criteria.";

    /**
     * Specifies the validation groups to which this constraint belongs.
     * This allows for conditional validation based on different scenarios or phases of an application's lifecycle.
     *
     * @return An array of classes representing the validation groups.
     * @see jakarta.validation.groups.Default The default validation group.
     */
    Class<?>[] groups() default {};

    /**
     * Defines the payload associated with this constraint.
     * Payloads can be used by clients of the Jakarta Bean Validation API to assign custom metadata
     * to constraint violations.
     *
     * @return An array of classes extending {@link jakarta.validation.Payload}.
     * @see jakarta.validation.Payload Carries client-specific data associated with a constraint violation.
     */
    Class<? extends Payload>[] payload() default {};
}
