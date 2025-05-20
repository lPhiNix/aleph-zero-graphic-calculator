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
        return new MathEvaluationResultResponse(
                request.expressions().stream()
                        .map(expr -> evaluateExpression(expr, request.data()))
                        .collect(Collectors.toList())
        );
    }

    private MathExpressionEvaluationDto evaluateExpression(MathExpressionDto expr, MathDataDto data) {
        MathExpressionType type = MathExpressionType.detectType(expr.expression());

        List<MathEvaluationDto> evaluations = switch (type) {
            case FUNCTION -> List.of(
                    processMathOperation(this::evaluate, expr, data, type, MathEvaluationType.EVALUATION),
                    processMathOperation(this::draw, expr, data, type, MathEvaluationType.DRAWING)
            );
            case ASSIGNMENT, EQUATION, MATRIX, VECTOR -> List.of(
                    processMathOperation(this::evaluate, expr, data, type, MathEvaluationType.EVALUATION)
            );
            case NUMERIC -> List.of(
                    processMathOperation(this::calculate, expr, data, type, MathEvaluationType.CALCULATION)
            );
            case UNKNOWN -> List.of(
                    processMathOperation(this::evaluate, expr, data, type, MathEvaluationType.EVALUATION),
                    processMathOperation(this::calculate, expr, data, type, MathEvaluationType.CALCULATION),
                    processMathOperation(this::draw, expr, data, type, MathEvaluationType.DRAWING)
            );
            case NONE -> Collections.emptyList();
        };

        mathEclipse.clean();

        return new MathExpressionEvaluationDto(expr.expression(), type, evaluations);
    }

    private MathEvaluationDto processMathOperation(
                    MathOperation operation,
                    MathExpressionDto expr,
                    MathDataDto data,
                    MathExpressionType type,
                    MathEvaluationType evalType
    ) {
        MathExpressionEvaluation result = operation.compute(expr, data);
        return new MathEvaluationDto(
                evalType,
                result.getExpressionEvaluated(),
                result.getEvaluationProblems().orElse(null)
        );
    }

    private MathExpressionEvaluation evaluate(MathExpressionDto expr, MathDataDto data) {
        return mathEclipse.evaluate(expr.expression());
    }

    private MathExpressionEvaluation calculate(MathExpressionDto expr, MathDataDto data) {
        return mathEclipse.calculate(expr.expression(), data.decimals());
    }

    private MathExpressionEvaluation draw(MathExpressionDto expr, MathDataDto data) {
        return mathEclipse.draw(expr.expression(), "x", data.origin(), data.bound());
    }

    /**
     * A functional interface for math operations on expressions.
     */
    @FunctionalInterface
    public interface MathOperation {
           MathExpressionEvaluation compute(
                   MathExpressionDto expression,
                   MathDataDto data
           );
    }
}