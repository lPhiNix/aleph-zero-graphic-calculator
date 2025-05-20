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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MathExpressionService {

    private final MathLibFacade mathEclipse;

    public MathExpressionService(MathLibFacade mathEclipse) {
        this.mathEclipse = mathEclipse;
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
        MathExpressionType type = MathExpressionType.detectType(expr.expression());
        String expression = expr.expression();
        List<MathEvaluationDto> evaluations = switch (type) {
            case FUNCTION -> evaluateFunction(expression, data);
            case ASSIGNMENT, EQUATION, MATRIX, VECTOR -> List.of(
                    processMathOperation(this::evaluate, expression, data, MathEvaluationType.EVALUATION)
            );
            case NUMERIC -> List.of(
                    processMathOperation(this::calculate, expression, data, MathEvaluationType.CALCULATION)
            );
            case UNKNOWN -> evaluateUnknown(expression, data);
            case NONE -> Collections.emptyList();
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

    private MathExpressionEvaluation evaluate(String expression, MathDataDto data) {
        return mathEclipse.evaluate(expression);
    }

    private MathExpressionEvaluation calculate(String expression, MathDataDto data) {
        return mathEclipse.calculate(expression, data.decimals());
    }

    private MathExpressionEvaluation draw(String expression, MathDataDto data) {
        return mathEclipse.draw(expression, "x", data.origin(), data.bound());
    }

    @FunctionalInterface
    public interface MathOperation {
        MathExpressionEvaluation compute(String expression, MathDataDto data);
    }
}
