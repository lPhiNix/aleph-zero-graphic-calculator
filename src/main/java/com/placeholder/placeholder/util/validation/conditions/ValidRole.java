package com.placeholder.placeholder.util.validation.conditions;

import com.placeholder.placeholder.util.validation.validator.RoleValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RoleValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRole {

    String message() default "Role does not exist";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
