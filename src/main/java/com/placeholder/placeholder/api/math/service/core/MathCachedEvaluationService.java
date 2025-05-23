package com.placeholder.placeholder.api.math.service.core;

import com.placeholder.placeholder.api.math.dto.request.MathDataDto;
import com.placeholder.placeholder.api.math.facade.MathExpressionEvaluation;
import com.placeholder.placeholder.api.math.facade.MathLibFacade;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class MathCachedEvaluationService {

    private final MathLibFacade mathEclipse;

    public MathCachedEvaluationService(MathLibFacade mathEclipse) {
        this.mathEclipse = mathEclipse;
    }

    @Cacheable(value = "evaluate", key = "#expression")
    public MathExpressionEvaluation evaluate(String expression) {
        return mathEclipse.evaluate(expression);
    }

    @Cacheable(value = "calculate", key = "#expression + '_' + #data.decimals()")
    public MathExpressionEvaluation calculate(String expression, MathDataDto data) {
        return mathEclipse.calculate(expression, data.decimals());
    }

    @Cacheable(value = "draw", key = "#expression + '_' + #data.origin() + '_' + #data.bound()")
    public MathExpressionEvaluation draw(String expression, MathDataDto data) {
        return mathEclipse.draw(expression, "x", data.origin(), data.bound());
    }

    public void clearEvaluator() {
        mathEclipse.clear();
    }

    public void stopRequest() {
        mathEclipse.stopRequest();
    }
}

