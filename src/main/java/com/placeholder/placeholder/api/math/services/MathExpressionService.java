package com.placeholder.placeholder.api.math.services;

import com.placeholder.placeholder.api.math.dto.response.ExpressionResultResponse;
import com.placeholder.placeholder.api.math.facade.MathExpressionEvaluation;
import com.placeholder.placeholder.api.math.facade.MathLibFacade;
import org.springframework.stereotype.Service;

@Service
public class MathExpressionService {

    private final MathLibFacade mathEclipse;

    public MathExpressionService(MathLibFacade mathEclipse) {
        this.mathEclipse = mathEclipse;
    }

    public ExpressionResultResponse getEvaluation(String expression) {
        return null;
    }

    public ExpressionResultResponse evaluate(String expression) {
        MathExpressionEvaluation evaluation = mathEclipse.evaluate(expression);
        return new ExpressionResultResponse(
                evaluation.getExpressionEvaluated(),
                evaluation.getEvaluationProblems().orElse(null)
        );
    }

    public String calculate(String expression, int decimals) {
        return mathEclipse.calculate(expression, decimals).getExpressionEvaluated();
    }
}
