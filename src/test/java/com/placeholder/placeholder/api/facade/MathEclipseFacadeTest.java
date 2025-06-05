package com.placeholder.placeholder.api.facade;

import com.placeholder.placeholder.api.math.facade.symja.MathEclipseConfig;
import com.placeholder.placeholder.api.math.facade.symja.MathEclipseFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MathEclipseFacadeTest {

    private static final int CALCULATION_DECIMALS = 17;
    private static final String GRAPHICS_SYMBOL = "Graphics";
    private static final String DRAW_VARIABLE = "x";

    private MathEclipseFacade mathEclipseFacade;

    @BeforeEach
    void setUp() {
        mathEclipseFacade = MathEclipseConfig.buildMathEclipseFacade();
    }

    @Test
    @DisplayName("Evaluate: valid expressions")
    void testEvaluateValidExpressions() {
        assertTrue(testEvaluate(
                "Expand[(x + 1)^2]",
                "1+2*x+x^2"
        ));
        assertTrue(testEvaluate(
                "Simplify[(x^2 - 1)/(x - 1)]",
                "1+x"
        ));
        assertTrue(testEvaluate(
                "Solve[x^2 - 1 == 0, x]",
                "x->1"
        ));
        assertTrue(testEvaluate(
                "Integrate[x^2, x]",
                "x^3/3"
        ));
        assertTrue(testEvaluate(
                "D[x^3 + 2*x^2 + x, {x, 2}]",
                "4+6*x"
        ));
        assertTrue(testEvaluate(
                "Limit[(1+1/x)^x, x -> Infinity]",
                "E"
        ));
        assertTrue(testEvaluate(
                "Inverse[{{1, 2}, {3, 4}}]",
                "{{-2,1},\n {3/2,-1/2}}"
        ));
        assertTrue(testEvaluate(
                "Transpose[{{1, 2}, {3, 4}}]",
                "{{1,3},\n {2,4}}"
        ));
        assertTrue(testEvaluate(
                "Eigenvalues[{{2, 1}, {1, 2}}]",
                "{3,1}"
        ));
        assertTrue(testEvaluate(
                "GCD[18, 24]", "6"
        ));
        assertTrue(testEvaluate(
                "LCM[18, 24]", "72"
        ));
    }

    private boolean testEvaluate(String expression, String expectedResult) {
        return rawTestEvaluate(expression).contains(expectedResult);
    }

    private String rawTestEvaluate(String expression) {
        return mathEclipseFacade.evaluate(expression).getExpressionEvaluated();
    }

    @Test
    @DisplayName("Calculate: numeric expressions")
    void testCalculate() {
        assertTrue(testCalculate(
                "E", "2.718281828459045"
        ));
        assertTrue(testCalculate(
                "Sqrt[2]", "1.414213562373095"
        ));
        assertTrue(testCalculate(
                "Log[10]", "2.3025850929940456"
        ));
    }

    private boolean testCalculate(String expression, String expectedResult) {
        return rawTestCalculate(expression).contains(expectedResult);
    }

    private String rawTestCalculate(String expression) {
        return mathEclipseFacade.calculate(expression, CALCULATION_DECIMALS).getExpressionEvaluated();
    }

    @Test
    @DisplayName("Draw: expresiones para plot")
    void testDraw() {
        assertTrue(testDraw(
                "Sin[x]", "-Pi", "Pi"
        ));
        assertTrue(testDraw(
                "sin(x)", "-500000", "500000"
        ));
        assertTrue(testDraw(
                "Tan[x]", "-Pi/2", "Pi/2"
        ));
    }

    private boolean testDraw(String expression, String origin, String bound) {
         String draw = rawTestDraw(expression, origin, bound);
         System.out.println(draw);
         return draw.contains(GRAPHICS_SYMBOL);
    }

    private String rawTestDraw(String expression, String origin, String bound) {
        return mathEclipseFacade.draw(expression, DRAW_VARIABLE, origin, bound).getExpressionEvaluated();
    }

    @Test
    @DisplayName("Stop Request: interrupt ongoing evaluation")
    void testStopRequest() {
        Thread evaluationThread = new Thread(() -> {
            try {
                String impossibleExpression = "Integrate[log(sin(x^2 + cos(x))) / (1 + x^6), x]";
                String result = mathEclipseFacade.evaluate(impossibleExpression).getExpressionEvaluated();
                fail("Expected stopRequest to interrupt the evaluation, but got: " + result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        evaluationThread.start();
        try {
            // Wait a bit to let the evaluation start
            Thread.sleep(100);
            // Stop the ongoing evaluation
            mathEclipseFacade.stopRequest();
            // Wait for the thread to complete
            evaluationThread.join();
        } catch (InterruptedException e) {
            fail("Test interrupted unexpectedly.");
        }
    }
}
