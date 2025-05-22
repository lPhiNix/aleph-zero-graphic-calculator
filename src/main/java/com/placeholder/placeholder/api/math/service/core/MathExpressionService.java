package com.placeholder.placeholder.api.math.service.core;

import com.placeholder.placeholder.api.math.dto.request.MathEvaluationRequest;
import com.placeholder.placeholder.api.math.dto.response.MathEvaluationDto;
import com.placeholder.placeholder.api.math.dto.response.MathEvaluationResultResponse;
import com.placeholder.placeholder.api.math.dto.response.MathExpressionEvaluationDto;
import com.placeholder.placeholder.api.math.enums.computation.MathExpressionType;
import com.placeholder.placeholder.api.math.service.classifier.Classifier;
import com.placeholder.placeholder.api.math.service.memory.MathAssignmentMemory;
import com.placeholder.placeholder.api.math.service.strategy.EvaluationStrategyContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MathExpressionService implements MathEvaluationService {

    private final MathAssignmentMemory memory;
    private final Classifier mathExpressionClassifier;
    private final EvaluationStrategyContext context;

    @Autowired
    public MathExpressionService(
            MathAssignmentMemory memory,
            Classifier mathExpressionClassifier,
            EvaluationStrategyContext context
    ) {
        this.memory = memory;
        this.mathExpressionClassifier = mathExpressionClassifier;
        this.context = context;
    }

    @Override
    public MathEvaluationResultResponse evaluation(MathEvaluationRequest request) {
        List<MathExpressionEvaluationDto> responses = request.expressions().stream().map(expr -> {
            String expressionToEvaluate = memory.process(expr.expression());
            MathExpressionType type = mathExpressionClassifier.classify(expressionToEvaluate);
            List<MathEvaluationDto> results = context.getStrategy(type).compute(expressionToEvaluate, request.data());
            return new MathExpressionEvaluationDto(expr.expression(), type, results);
        }).toList();

        memory.clear();

        return new MathEvaluationResultResponse(responses);
    }
}