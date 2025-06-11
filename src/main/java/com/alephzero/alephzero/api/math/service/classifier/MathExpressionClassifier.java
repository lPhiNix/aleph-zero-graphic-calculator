package com.alephzero.alephzero.api.math.service.classifier;

import com.alephzero.alephzero.api.math.dto.request.MathDataDto;
import com.alephzero.alephzero.api.math.enums.computation.MathExpressionType;
import com.alephzero.alephzero.api.math.regex.RegexValidator;
import com.alephzero.alephzero.api.math.service.core.MathCachedEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.alephzero.alephzero.api.math.enums.computation.MathExpressionType.*;

/**
 * {@code MathExpressionClassifier} classifies mathematical expressions into
 * different types defined by {@link MathExpressionType}, based on syntactic and
 * semantic analysis.
 * <p>
 * It performs classification by validating the expression format using regular
 * expressions, pre-evaluating expressions, and calculating partial results when necessary.
 * <p>
 * This component is a Spring {@link Component} and uses dependency injection to
 * access the {@link MathCachedEvaluationService} and {@link RegexValidator}.
 */
@Component
public class MathExpressionClassifier implements Classifier {

    private final MathCachedEvaluationService mathEclipse;
    private final RegexValidator regexValidator;

    /**
     * Constructs a new {@code MathExpressionClassifier} with the required dependencies.
     *
     * @param mathEclipse    the evaluation service used for pre-evaluation and calculation
     * @param regexValidator the regex validator used for raw classification
     */
    @Autowired
    public MathExpressionClassifier(
            MathCachedEvaluationService mathEclipse,
            RegexValidator regexValidator
    ) {
        this.mathEclipse = mathEclipse;
        this.regexValidator = regexValidator;
    }

    /**
     * Classifies the type of mathematical expression string.
     * <p>
     * The classification logic combines multiple steps:
     * <ul>
     *     <li>Check if the expression is null or empty returning {@link MathExpressionType#NONE}</li>
     *     <li>Classify after pre-evaluation of the expression</li>
     *     <li>Classify the raw expression using regex patterns</li>
     *     <li>Handle special cases like {@link MathExpressionType#ASSIGNMENT}</li>
     *     <li>Classify after pre-calculation if the expression appears to be a function</li>
     *     <li>Fallback to unknown if no other type matches</li>
     * </ul>
     *
     * @param expression the mathematical expression to classify
     * @return the determined {@link MathExpressionType}
     */
    @Override
    public MathExpressionType classify(String expression) {
        if (isNullOrEmpty(expression)) {
            return NONE;
        }

        MathExpressionType preEvalType = classifyPreEvaluation(expression);
        MathExpressionType rawType = rawClassify(expression);

        if (rawType == ASSIGNMENT) {
            return ASSIGNMENT;
        }

        if (preEvalType == FUNCTION) {
            MathExpressionType preCalcType = classifyPreCalculation(expression);
            if (preCalcType == NUMERIC) {
                return NUMERIC;
            } else if (preCalcType == FUNCTION) {
                return FUNCTION;
            }
        }

        return preEvalType != UNKNOWN ? preEvalType : rawType;
    }

    /**
     * Checks if a string is null or empty after trimming.
     *
     * @param str the string to check
     * @return {@code true} if the string is null or empty after trimming, otherwise {@code false}
     */
    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Performs a preliminary classification of the expression by evaluating it first
     * and then classifying the evaluated result.
     *
     * @param expression the expression to pre-evaluate and classify
     * @return the {@link MathExpressionType} of the evaluated expression
     */
    private MathExpressionType classifyPreEvaluation(String expression) {
        String evaluatedExpr = mathEclipse.evaluate(expression).getExpressionEvaluated();
        return rawClassify(evaluatedExpr);
    }

    /**
     * Performs a preliminary classification of the expression by calculating it with
     * default data and then classifying the result.
     *
     * @param expression the expression to calculate and classify
     * @return the {@link MathExpressionType} of the calculated expression
     */
    private MathExpressionType classifyPreCalculation(String expression) {
        MathDataDto data = new MathDataDto(1, null, null);
        String calculatedExpr = mathEclipse.calculate(expression, data).getExpressionEvaluated();
        return rawClassify(calculatedExpr);
    }

    /**
     * Classifies the expression string using raw regular expression patterns.
     * <p>
     * The method attempts to match the expression against known patterns for
     * equations, booleans, assignments, matrices, vectors, numerics, and functions.
     * If no pattern matches, it returns {@link MathExpressionType#UNKNOWN}.
     *
     * @param expression the raw expression string to classify
     * @return the matching {@link MathExpressionType} or {@link MathExpressionType#UNKNOWN} if none match
     */
    public MathExpressionType rawClassify(String expression) {
        String trimmed = expression.trim();

        if (regexValidator.EQUATION_PATTERN.matcher(trimmed).matches()) return MathExpressionType.EQUATION;
        if (regexValidator.BOOLEAN_PATTERN.matcher(trimmed).matches()) return MathExpressionType.BOOLEAN;
        if (regexValidator.ASSIGNMENT_PATTERN.matcher(trimmed).matches()) return MathExpressionType.ASSIGNMENT;
        if (regexValidator.MATRIX_PATTERN.matcher(trimmed).matches()) return MathExpressionType.MATRIX;
        if (regexValidator.VECTOR_PATTERN.matcher(trimmed).matches()) return MathExpressionType.VECTOR;
        if (regexValidator.NUMERIC_PATTERN.matcher(trimmed).matches()) return MathExpressionType.NUMERIC;
        if (regexValidator.FUNCTION_PATTERN.matcher(trimmed).matches()) return FUNCTION;

        return MathExpressionType.UNKNOWN;
    }
}
