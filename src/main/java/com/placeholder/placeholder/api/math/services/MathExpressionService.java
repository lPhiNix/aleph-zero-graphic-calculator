package com.placeholder.placeholder.api.math.services;

import com.placeholder.placeholder.api.math.dto.request.MathDataDto;
import com.placeholder.placeholder.api.math.dto.request.MathEvaluationRequest;
import com.placeholder.placeholder.api.math.dto.request.MathExpressionDto;
import com.placeholder.placeholder.api.math.dto.response.MathEvaluationDto;
import com.placeholder.placeholder.api.math.dto.response.MathEvaluationResultResponse;
import com.placeholder.placeholder.api.math.dto.response.MathExpressionEvaluationDto;
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

        MathExpressionType type = checkExpressionType(expression);

        switch (type) {
            case NONE:
                MathEvaluationDto evaluation =  processMathOperation(this::evaluate, mathExpression, data, type);
                MathEvaluationDto calculation =  processMathOperation(this::calculate, mathExpression, data, type);
                MathEvaluationDto representation =  processMathOperation(this::draw, mathExpression, data, type);

                List<MathEvaluationDto> list = Arrays.asList(evaluation, calculation, representation);
                return new MathExpressionEvaluationDto(expression, list);
            default:
                return null;
        }
    }

    private MathExpressionType checkExpressionType(String expression) {
        return MathExpressionType.NONE;
    }

    private MathEvaluationDto processMathOperation(
            MathOperation mathOperation,
            MathExpressionDto mathExpression,
            MathDataDto data,
            MathExpressionType type
    ) {
        return mathOperation.compute(mathExpression, data, type);
    }


    private MathEvaluationDto evaluate(
            MathExpressionDto mathExpression,
            MathDataDto data,
            MathExpressionType type
    ) {
        String expression = mathExpression.expression();
        MathExpressionEvaluation evaluationResult = mathEclipse.evaluate(expression);
        return map(evaluationResult, type);
    }

    private MathEvaluationDto calculate(
            MathExpressionDto mathExpression,
            MathDataDto data,
            MathExpressionType type
    ) {
        String expression = mathExpression.expression();
        int decimals = data.decimals();
        MathExpressionEvaluation evaluationResult = mathEclipse.calculate(expression, decimals);
        return map(evaluationResult, type);
    }

    private MathEvaluationDto draw(
            MathExpressionDto mathExpression,
            MathDataDto data,
            MathExpressionType type
    ) {
        String expression = mathExpression.expression();
        String origin = data.origin();
        String bound = data.bound();
        MathExpressionEvaluation evaluationResult = mathEclipse.draw(expression, "x", origin, bound);
        return map(evaluationResult, type);
    }

    private MathEvaluationDto map(MathExpressionEvaluation evaluationResult, MathExpressionType type) {
        return new MathEvaluationDto(
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
                MathExpressionType type
        );
    }
}
