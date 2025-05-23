package com.placeholder.placeholder.api.math.validation.symja.annotations;

import com.placeholder.placeholder.api.math.validation.symja.validator.DecimalsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DecimalsValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDecimals {
    String message() default "Invalid decimals value.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}