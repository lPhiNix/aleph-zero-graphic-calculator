package com.placeholder.placeholder.api.facade;

public interface MathLibFacade {
    String validate(String expression);
    String evaluate(String expression);
    String calculate(String expression, int decimals);
    String draw(String expression, String variable, String origin, String bound);

    String formatResult(String expression);
}
