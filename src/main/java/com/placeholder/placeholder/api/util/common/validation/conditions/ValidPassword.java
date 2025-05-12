package com.placeholder.placeholder.api.util.common.validation.conditions;

import com.placeholder.placeholder.api.util.common.validation.validator.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {

    String message() default "Password is not valid. Please meet all the criteria.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
