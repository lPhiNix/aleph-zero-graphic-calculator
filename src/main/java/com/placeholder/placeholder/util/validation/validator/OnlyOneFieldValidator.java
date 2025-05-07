package com.placeholder.placeholder.util.validation.validator;

import com.placeholder.placeholder.util.validation.conditions.OnlyOneField;
import jakarta.validation.ConstraintDeclarationException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class OnlyOneFieldValidator implements ConstraintValidator<OnlyOneField, Object> {

    private String[] fields;

    @Override
    public void initialize(OnlyOneField constraintAnnotation) {
        this.fields = constraintAnnotation.fields();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            int count = 0;
            for (String fieldName : fields) {
                Field field = value.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                Object fieldValue = field.get(value);
                if (fieldValue != null) {
                    count++;
                }
            }
            // Only one of the fields must be not null
            return count == 1;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ConstraintDeclarationException("Invalid validation configuration.");
        }
    }
}
