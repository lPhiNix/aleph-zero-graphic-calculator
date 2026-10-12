package com.alephzero.alephzero.core.services.math.validation.symja.annotations;

import com.alephzero.alephzero.core.services.math.validation.symja.validator.MathEclipseExpressionValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MathEclipseExpressionValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMathEclipseExpression {
    String message() default "Invalid math expression.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
