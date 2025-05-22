package com.placeholder.placeholder.api.math.service.classifier;

import com.placeholder.placeholder.api.math.enums.computation.MathExpressionType;
import com.placeholder.placeholder.api.math.regex.RegexValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MathExpressionClassifier implements Classifier {

    private final RegexValidator regexValidator;

    @Autowired
    public MathExpressionClassifier(RegexValidator regexValidator) {
        this.regexValidator = regexValidator;
    }

    @Override
    public MathExpressionType classify(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            return MathExpressionType.NONE;
        }

        String trimmedExpr = expression.trim();
        if (regexValidator.EQUATION_PATTERN.matcher(trimmedExpr).matches()) {
            return MathExpressionType.EQUATION;
        } else if (regexValidator.BOOLEAN_PATTERN.matcher(trimmedExpr).matches()) {
            return MathExpressionType.BOOLEAN;
        } else if (regexValidator.ASSIGNMENT_PATTERN.matcher(trimmedExpr).matches()) {
            return MathExpressionType.ASSIGNMENT;
        } else if (regexValidator.MATRIX_PATTERN.matcher(trimmedExpr).matches()) {
            return MathExpressionType.MATRIX;
        } else if (regexValidator.VECTOR_PATTERN.matcher(trimmedExpr).matches()) {
            return MathExpressionType.VECTOR;
        } else if (regexValidator.NUMERIC_PATTERN.matcher(trimmedExpr).matches()) {
            return MathExpressionType.NUMERIC;
        } else if (regexValidator.FUNCTION_PATTERN.matcher(trimmedExpr).matches()) {
            return MathExpressionType.FUNCTION;
        }

        return MathExpressionType.UNKNOWN;
    }
}
