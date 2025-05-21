package com.placeholder.placeholder.api.math.service.core;

import com.placeholder.placeholder.api.math.dto.request.MathEvaluationRequest;
import com.placeholder.placeholder.api.math.dto.response.MathEvaluationDto;
import com.placeholder.placeholder.api.math.dto.response.MathEvaluationResultResponse;
import com.placeholder.placeholder.api.math.dto.response.MathExpressionEvaluationDto;
import com.placeholder.placeholder.api.math.enums.MathExpressionType;
import com.placeholder.placeholder.api.math.service.memory.MathAssignmentMemory;
import com.placeholder.placeholder.api.math.service.micro.MathExpressionClassifier;
import com.placeholder.placeholder.api.math.service.strategy.EvaluationStrategyContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MathExpressionService {

    private final MathAssignmentMemory memory;
    private final MathExpressionClassifier typeDetector;
    private final EvaluationStrategyContext context;

    public MathExpressionService(
            MathAssignmentMemory memory,
            MathExpressionClassifier typeDetector,
            EvaluationStrategyContext context
    ) {
        this.memory = memory;
        this.typeDetector = typeDetector;
        this.context = context;
    }

    public MathEvaluationResultResponse evaluation(MathEvaluationRequest request) {
        List<MathExpressionEvaluationDto> responses = request.expressions().stream().map(expr -> {
            String expressionToEvaluate = memory.process(expr.expression());
            MathExpressionType type = typeDetector.detectType(expressionToEvaluate);
            List<MathEvaluationDto> results = context.getStrategy(type).compute(expressionToEvaluate, request.data());
            return new MathExpressionEvaluationDto(expr.expression(), type, results);
        }).toList();

        return new MathEvaluationResultResponse(responses);
    }
}
