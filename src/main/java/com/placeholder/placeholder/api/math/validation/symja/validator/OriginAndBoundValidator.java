package com.placeholder.placeholder.api.math.validation.symja.validator;

import com.placeholder.placeholder.api.math.dto.request.MathDataDto;
import com.placeholder.placeholder.api.math.regex.RegexValidator;
import com.placeholder.placeholder.api.math.service.core.MathCachedEvaluationService;
import com.placeholder.placeholder.api.math.validation.symja.annotations.ValidOriginAndBound;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OriginAndBoundValidator implements ConstraintValidator<ValidOriginAndBound, MathDataDto> {

    private final RegexValidator regexValidator;
    private final MathCachedEvaluationService mathEclipse;

    public OriginAndBoundValidator(RegexValidator regexValidator, MathCachedEvaluationService mathEclipse) {
        this.regexValidator = regexValidator;
        this.mathEclipse = mathEclipse;
    }

    @Override
    public boolean isValid(MathDataDto data, ConstraintValidatorContext context) {
        if (data == null) return true;

        String origin = data.origin();
        String bound = data.bound();

        if (origin == null || origin.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Origin cannot be null or empty.")
                    .addPropertyNode("origin")
                    .addConstraintViolation();
            return false;
        }

        if (bound == null || bound.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Bound cannot be null or empty.")
                    .addPropertyNode("bound")
                    .addConstraintViolation();
            return false;
        }

        if (!RegexValidator.match(origin, regexValidator.NUMERIC_PATTERN)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Origin does not match numeric pattern.")
                    .addPropertyNode("origin")
                    .addConstraintViolation();
            return false;
        }

        if (!RegexValidator.match(bound, regexValidator.NUMERIC_PATTERN)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Bound does not match numeric pattern.")
                    .addPropertyNode("bound")
                    .addConstraintViolation();
            return false;
        }

        try {
            MathDataDto dummy = new MathDataDto(1, "", ""); // Dummy for context
            String originEvaluated = mathEclipse.calculate(origin, dummy).getExpressionEvaluated();
            String boundEvaluated = mathEclipse.calculate(bound, dummy).getExpressionEvaluated();

            BigDecimal originValue = new BigDecimal(originEvaluated);
            BigDecimal boundValue = new BigDecimal(boundEvaluated);

            if (originValue.compareTo(boundValue) >= 0) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Origin must be less than bound.")
                        .addPropertyNode("origin")
                        .addConstraintViolation();
                return false;
            }
        } catch (Exception e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Failed to evaluate expressions.")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
