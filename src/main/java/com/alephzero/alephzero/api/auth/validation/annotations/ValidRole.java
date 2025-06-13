package com.alephzero.alephzero.api.auth.validation.annotations;

import com.alephzero.alephzero.api.auth.validation.validator.RoleValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom Jakarta Bean Validation annotation used to validate if a string value
 * represents a valid user role.
 * <p>
 * This annotation is processed by the associated {@link RoleValidator} class, which checks if the
 * annotated field's value exists in the database.
 * </p>
 */
@Documented
@Constraint(validatedBy = RoleValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRole {

    /**
     * Defines the default error message that will be generated if the validation performed by
     * {@link RoleValidator} fails.
     *
     * @return The error message indicating that the role does not exist.
     */
    String message() default "Role does not exist";

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