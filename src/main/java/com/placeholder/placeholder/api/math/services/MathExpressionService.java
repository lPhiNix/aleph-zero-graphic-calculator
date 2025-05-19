package com.placeholder.placeholder.api.math.services;

import com.placeholder.placeholder.api.math.dto.request.MathDataDto;
import com.placeholder.placeholder.api.math.dto.request.MathEvaluationRequest;
import com.placeholder.placeholder.api.math.dto.request.MathExpressionDto;
import com.placeholder.placeholder.api.math.dto.response.MathEvaluationDto;
import com.placeholder.placeholder.api.math.dto.response.MathEvaluationResultResponse;
import com.placeholder.placeholder.api.math.dto.response.MathExpressionEvaluationDto;
import com.placeholder.placeholder.api.math.enums.MathEvaluationType;
import com.placeholder.placeholder.api.math.enums.MathExpressionType;
import com.placeholder.placeholder.api.math.facade.MathExpressionEvaluation;
import com.placeholder.placeholder.api.math.facade.MathLibFacade;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class MathExpressionService {

    private final MathLibFacade mathEclipse;

    public MathExpressionService(MathLibFacade mathEclipse) {
        this.mathEclipse = mathEclipse;
    }

    public MathEvaluationResultResponse evaluation(MathEvaluationRequest mathEvaluationRequest) {
        List<MathExpressionDto> expressions = mathEvaluationRequest.expressions();
        MathDataDto data = mathEvaluationRequest.data();

        List<MathExpressionEvaluationDto> evaluations = new ArrayList<>();

        for (MathExpressionDto expression : expressions) {
            evaluations.add(evaluateExpression(expression, data));
        }

        return new MathEvaluationResultResponse(evaluations);
    }

    private MathExpressionEvaluationDto evaluateExpression(
            MathExpressionDto mathExpression,
            MathDataDto data
    ) {
        String expression = mathExpression.expression();

        MathExpressionType type = MathExpressionType.detectType(expression);

        MathEvaluationDto evaluation, calculation, representation;

        switch (type) {
            case FUNCTION:
                evaluation = processMathOperation(this::evaluate, mathExpression, data, type, MathEvaluationType.EVALUATION);
                representation = processMathOperation(this::draw, mathExpression, data, type, MathEvaluationType.DRAWING);

                return new MathExpressionEvaluationDto(expression, createOperationList(
                        evaluation, representation
                ));
            case ASSIGNMENT:
                evaluation = processMathOperation(this::evaluate, mathExpression, data, type, MathEvaluationType.EVALUATION);

                return new MathExpressionEvaluationDto(expression, createOperationList(
                        evaluation
                ));
            case EQUATION:
                evaluation = processMathOperation(this::evaluate, mathExpression, data, type, MathEvaluationType.EVALUATION);

                return new MathExpressionEvaluationDto(expression, createOperationList(
                        evaluation
                ));
            case NUMERIC:
                calculation = processMathOperation(this::calculate, mathExpression, data, type, MathEvaluationType.CALCULATION);

                return new MathExpressionEvaluationDto(expression, createOperationList(
                        calculation
                ));
            case MATRIX:
                evaluation = processMathOperation(this::evaluate, mathExpression, data, type, MathEvaluationType.EVALUATION);

                return new MathExpressionEvaluationDto(expression, createOperationList(
                        evaluation
                ));
            case VECTOR:
                evaluation = processMathOperation(this::evaluate, mathExpression, data, type, MathEvaluationType.EVALUATION);

                return new MathExpressionEvaluationDto(expression, createOperationList(
                        evaluation
                ));
            case UNKNOWN:
                evaluation = processMathOperation(this::evaluate, mathExpression, data, type, MathEvaluationType.EVALUATION);
                calculation = processMathOperation(this::calculate, mathExpression, data, type, MathEvaluationType.CALCULATION);
                representation = processMathOperation(this::draw, mathExpression, data, type, MathEvaluationType.DRAWING);

                return new MathExpressionEvaluationDto(expression, createOperationList(
                        evaluation, calculation, representation
                ));
            default:
                return new MathExpressionEvaluationDto(expression);
        }
    }

    private List<MathEvaluationDto> createOperationList(MathEvaluationDto... evaluations) {
        return Arrays.asList(evaluations);
    }

    private MathEvaluationDto processMathOperation(
            MathOperation mathOperation,
            MathExpressionDto mathExpression,
            MathDataDto data,
            MathExpressionType type,
            MathEvaluationType evaluationType
    ) {
        return mathOperation.compute(mathExpression, data, type, evaluationType);
    }


    private MathEvaluationDto evaluate(
            MathExpressionDto mathExpression,
            MathDataDto data,
            MathExpressionType type,
            MathEvaluationType evaluationType
    ) {
        String expression = mathExpression.expression();
        MathExpressionEvaluation evaluationResult = mathEclipse.evaluate(expression);
        return map(evaluationResult, type, evaluationType);
    }

    private MathEvaluationDto calculate(
            MathExpressionDto mathExpression,
            MathDataDto data,
            MathExpressionType type,
            MathEvaluationType evaluationType
    ) {
        String expression = mathExpression.expression();
        int decimals = data.decimals();
        MathExpressionEvaluation evaluationResult = mathEclipse.calculate(expression, decimals);
        return map(evaluationResult, type, evaluationType);
    }

    private MathEvaluationDto draw(
            MathExpressionDto mathExpression,
            MathDataDto data,
            MathExpressionType type,
            MathEvaluationType evaluationType
    ) {
        String expression = mathExpression.expression();
        String origin = data.origin();
        String bound = data.bound();
        MathExpressionEvaluation evaluationResult = mathEclipse.draw(expression, "x", origin, bound);
        return map(evaluationResult, type, evaluationType);
    }

    private MathEvaluationDto map(MathExpressionEvaluation evaluationResult, MathExpressionType type, MathEvaluationType evaluationType) {
        return new MathEvaluationDto(
                evaluationType,
                type,
                evaluationResult.getExpressionEvaluated(),
                evaluationResult.getEvaluationProblems().orElse(null)
        );
    }

    /**
     * A functional interface for math operations on expressions.
     */
    @FunctionalInterface
    public interface MathOperation {
        MathEvaluationDto compute(
                MathExpressionDto expression,
                MathDataDto data,
                MathExpressionType type,
                MathEvaluationType evaluationType
        );
    }
}
