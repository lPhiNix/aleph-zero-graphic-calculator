package com.placeholder.placeholder.api.math.services.micro;

import com.placeholder.placeholder.api.math.dto.request.MathDataDto;
import com.placeholder.placeholder.api.math.facade.MathExpressionEvaluation;
import com.placeholder.placeholder.api.math.facade.MathLibFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class MathEvaluationCachedService {

    @Autowired
    private MathLibFacade mathEclipse;

    @Cacheable(value = "evaluate", key = "#expression")
    public MathExpressionEvaluation evaluate(String expression, MathDataDto data) {
        System.out.println("CACHED EVAL: " + expression);
        return mathEclipse.evaluate(expression);
    }

    @Cacheable(value = "calculate", key = "#expression + '_' + #data.decimals()")
    public MathExpressionEvaluation calculate(String expression, MathDataDto data) {
        System.out.println("CACHED CALC: " + expression + "_" + data.decimals());
        return mathEclipse.calculate(expression, data.decimals());
    }

    @Cacheable(value = "draw", key = "#expression + '_' + #data.origin() + '_' + #data.bound()")
    public MathExpressionEvaluation draw(String expression, MathDataDto data) {
        System.out.println("CACHED DRAW: " + expression + "_" + data.origin() + "_" + data.bound());
        return mathEclipse.draw(expression, "x", data.origin(), data.bound());
    }
}

