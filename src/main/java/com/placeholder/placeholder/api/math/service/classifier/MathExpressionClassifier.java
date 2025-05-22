package com.placeholder.placeholder.api.math.service.classifier;

import com.placeholder.placeholder.api.math.dto.request.MathDataDto;
import com.placeholder.placeholder.api.math.enums.computation.MathExpressionType;
import com.placeholder.placeholder.api.math.regex.RegexValidator;
import com.placeholder.placeholder.api.math.service.core.MathEvaluationCached;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.placeholder.placeholder.api.math.enums.computation.MathExpressionType.*;

@Component
public class MathExpressionClassifier implements Classifier {

    private final MathEvaluationCached mathEclipse;
    private final RegexValidator regexValidator;

    @Autowired
    public MathExpressionClassifier(
            MathEvaluationCached mathEclipse,
            RegexValidator regexValidator
    ) {
        this.mathEclipse = mathEclipse;
        this.regexValidator = regexValidator;
    }

    @Override
    public MathExpressionType classify(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            return MathExpressionType.NONE;
        }

        String preEvaluation = preEvaluation(expression);

        MathExpressionType preEvaluationExpressionType = rawClassify(preEvaluation);
        MathExpressionType expressionType = rawClassify(expression);


        if (expressionType == ASSIGNMENT) {
            return ASSIGNMENT;
        }

        if (preEvaluationExpressionType == FUNCTION) {
            String preCalculation = preCalculation(expression);
            MathExpressionType preCalculationExpressionType = rawClassify(preCalculation);

            if (preCalculationExpressionType == NUMERIC) {
                return NUMERIC;
            } else if (preCalculationExpressionType == FUNCTION) {
                return FUNCTION;
            }
        }

        if (preEvaluationExpressionType != UNKNOWN) {
            return preEvaluationExpressionType;
        }

        return expressionType;
    }

    private String preEvaluation(String expression) {
        return mathEclipse.evaluate(expression).getExpressionEvaluated();
    }

    private String preCalculation(String expression) {
        MathDataDto data = new MathDataDto(1, null, null);
        return mathEclipse.calculate(expression, data).getExpressionEvaluated();
    }

    public MathExpressionType rawClassify(String expression) {
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
            return FUNCTION;
        }

        return MathExpressionType.UNKNOWN;
    }
}
