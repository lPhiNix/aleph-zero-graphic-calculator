package com.placeholder.placeholder.api.math.service.classifier;

import com.placeholder.placeholder.api.math.enums.computation.MathExpressionType;
import com.placeholder.placeholder.api.math.regex.RegexValidator;
import com.placeholder.placeholder.api.math.service.core.MathEvaluationCached;
import com.placeholder.placeholder.api.math.service.memory.MathAssignmentMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Function;

import static com.placeholder.placeholder.api.math.enums.computation.MathExpressionType.*;

@Component
public class MathExpressionClassifierPro implements Classifier {

    private final MathEvaluationCached mathEclipse;
    private final MathAssignmentMemory assignmentMemory;
    private final RegexValidator regexValidator;

    @Autowired
    public MathExpressionClassifierPro(
            MathEvaluationCached mathEclipse,
            MathAssignmentMemory assignmentMemory,
            RegexValidator regexValidator
    ) {
        this.mathEclipse = mathEclipse;
        this.assignmentMemory = assignmentMemory;
        this.regexValidator = regexValidator;
    }

    @Override
    public MathExpressionType classify(String expression) {
        return null;
    }

    private String preEvaluation(String expression) {
        return mathEclipse.evaluate(expression).getExpressionEvaluated();
    }

    private MathExpressionType c(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            return MathExpressionType.NONE;
        }

        String preEvaluation = preEvaluation(expression);
        MathExpressionType preEvaluationExpressionType = peruanC(preEvaluation);

        switch (preEvaluationExpressionType) {




        }

        return MathExpressionType.UNKNOWN;
    }

    public MathExpressionType peruanC(String expression) {
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
