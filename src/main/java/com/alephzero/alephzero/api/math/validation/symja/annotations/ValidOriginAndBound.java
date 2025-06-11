package com.alephzero.alephzero.api.math.validation.symja.annotations;

import com.alephzero.alephzero.api.math.validation.symja.validator.OriginAndBoundValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = OriginAndBoundValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidOriginAndBound {
    String message() default "Invalid origin and bound values draw.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
