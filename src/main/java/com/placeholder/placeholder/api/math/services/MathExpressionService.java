package com.placeholder.placeholder.api.math.services;

import com.placeholder.placeholder.api.math.services.cache.MathAssignmentMemory;
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
import com.placeholder.placeholder.api.math.services.micro.MathExpressionTypeDetector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service @Lazy
public class MathExpressionService {

    private final MathLibFacade mathEclipse;
    private final MathAssignmentMemory assignmentMemory;
    private final MathExpressionTypeDetector expressionTypeDetector;

    @Autowired
    public MathExpressionService(
            MathLibFacade mathEclipse,
            MathAssignmentMemory assignmentMemory,
            MathExpressionTypeDetector expressionTypeDetector
    ) {
        this.mathEclipse = mathEclipse;
        this.assignmentMemory = assignmentMemory;
        this.expressionTypeDetector = expressionTypeDetector;
    }

    public MathEvaluationResultResponse evaluation(MathEvaluationRequest request) {
        MathEvaluationResultResponse response = new MathEvaluationResultResponse(
                request.expressions().stream()
                        .map(expr -> evaluateExpression(expr, request.data()))
                        .collect(Collectors.toList())
        );

        mathEclipse.clean();
        return response;
    }

    private MathExpressionEvaluationDto evaluateExpression(MathExpressionDto expr, MathDataDto data) {
        String expression = assignmentMemory.process(expr.expression());

        MathExpressionType type = expressionTypeDetector.detectType(expression);

        List<MathEvaluationDto> evaluations = switch (type) {
            case FUNCTION -> evaluateFunction(expression, data);
            case EQUATION, MATRIX, VECTOR -> List.of(
                    processMathOperation(this::evaluate, expression, data, MathEvaluationType.EVALUATION)
            );
            case NUMERIC -> List.of(
                    processMathOperation(this::calculate, expression, data, MathEvaluationType.CALCULATION)
            );
            case UNKNOWN -> evaluateUnknown(expression, data);
            case NONE, ASSIGNMENT -> Collections.emptyList();
        };

        return new MathExpressionEvaluationDto(expr.expression(), type, evaluations);
    }


    private List<MathEvaluationDto> evaluateFunction(String expression, MathDataDto data) {
        MathEvaluationDto evaluationDto = processMathOperation(this::evaluate, expression, data, MathEvaluationType.EVALUATION);
        MathEvaluationDto drawingDto = processMathOperation(this::draw, expression, data, MathEvaluationType.DRAWING);
        return List.of(evaluationDto, drawingDto);
    }

    private List<MathEvaluationDto> evaluateUnknown(String expression, MathDataDto data) {
        MathEvaluationDto evaluationDto = processMathOperation(this::evaluate, expression, data, MathEvaluationType.EVALUATION);
        MathEvaluationDto calcDto = processMathOperation(this::calculate, expression, data, MathEvaluationType.CALCULATION);
        MathEvaluationDto drawDto = processMathOperation(this::draw, evaluationDto.evaluation(), data, MathEvaluationType.DRAWING);
        return List.of(evaluationDto, calcDto, drawDto);
    }

    private MathEvaluationDto processMathOperation(
            MathOperation operation,
            String expression,
            MathDataDto data,
            MathEvaluationType evalType
    ) {
        MathExpressionEvaluation result = operation.compute(expression, data);
        return new MathEvaluationDto(
                evalType,
                result.getExpressionEvaluated(),
                result.getEvaluationProblems().orElse(null)
        );
    }

    @Cacheable(value = "evaluate", key = "#expression")
    public MathExpressionEvaluation evaluate(String expression, MathDataDto data) {
        System.out.println("Evaluating expression: " + expression);
        return mathEclipse.evaluate(expression);
    }

    @Cacheable(value = "calculate", key = "#expression + '_' + #data.decimals()")
    public MathExpressionEvaluation calculate(String expression, MathDataDto data) {
        System.out.println("Calculating expression: " + expression + "_" + data.decimals());
        return mathEclipse.calculate(expression, data.decimals());
    }

    @Cacheable(value = "draw", key = "#expression + '_' + #data.origin() + '_' + #data.bound()")
    public MathExpressionEvaluation draw(String expression, MathDataDto data) {
        System.out.println("Evaluating expression: " + expression + "_" + data.origin() + "_" + data.bound());
        return mathEclipse.draw(expression, "x", data.origin(), data.bound());
    }

    @FunctionalInterface
    private interface MathOperation {
        MathExpressionEvaluation compute(String expression, MathDataDto data);
    }
}
