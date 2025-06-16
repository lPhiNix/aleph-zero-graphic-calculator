package com.alephzero.alephzero.api.auth.validation.annotations;

import com.alephzero.alephzero.api.auth.validation.validator.ValidRegisterFormValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom validation annotation for user registration forms.
 * <p>
 * This annotation is used to validate that the registration form meets specific criteria,
 * such as ensuring that the username and email are unique.
 * </p>
 * <p>
 * It is applied at the class level and uses the {@link ValidRegisterFormValidator} to perform
 * the validation logic.
 * </p>
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidRegisterFormValidator.class)
@Documented
public @interface ValidRegisterForm {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

