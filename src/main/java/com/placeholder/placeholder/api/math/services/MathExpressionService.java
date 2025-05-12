package com.placeholder.placeholder.api.math.services;

import com.placeholder.placeholder.api.math.dto.ExpressionResultResponse;
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

    public String evaluate(String expression) {
        return mathEclipse.evaluate(expression);
    }

    public String calculate(String expression, int decimals) {
        return mathEclipse.calculate(expression, decimals);
    }
}
