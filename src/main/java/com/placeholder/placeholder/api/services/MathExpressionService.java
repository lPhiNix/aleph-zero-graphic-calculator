package com.placeholder.placeholder.api.services;

import com.placeholder.placeholder.api.facade.MathEclipseFacade;
import org.springframework.stereotype.Service;

@Service
public class MathExpressionService {

    private final MathEclipseFacade mathEclipse;

    public MathExpressionService(MathEclipseFacade mathEclipse) {
        this.mathEclipse = mathEclipse;
    }

    public String evaluate(String expression) {
        return mathEclipse.evaluate(expression);
    }

    public String calculate(String expression, int decimals) {
        return mathEclipse.calculate(expression, decimals);
    }

    public String draw(String expression, String variable, String origin, String bound) {
        return mathEclipse.draw(expression, variable, origin, bound);
    }
}
