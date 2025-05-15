package com.placeholder.placeholder.api.facade;

import com.placeholder.placeholder.api.math.facade.symja.MathEclipseConfig;
import com.placeholder.placeholder.api.math.facade.symja.MathEclipseExpressionValidator;
import com.placeholder.placeholder.api.math.facade.symja.MathEclipseFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

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
    @DisplayName("Validate: valid expressions")
    void testValidateValidExpressions() {
        assertEquals(
                "x^2+2*x+1",
                testValidate("x^2 + 2*x + 1")
        );
        assertEquals(
                "Integrate[Sin[x],x]",
                testValidate("Integrate[Sin[x], x]")
        );
        assertEquals(
                "D[x^3,x]",
                testValidate("D[x^3, x]")
        );
        assertEquals(
                "Solve[x^2==1,x]",
                testValidate("Solve[x^2 == 1, x]")
        );
    }

    private String testValidate(String expression) {
        return mathEclipseFacade.validate(expression);
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
                "PrimeQ[7]", "true"
        ));
        assertTrue(testEvaluate(
                "GCD[18, 24]", "6"
        ));
        assertTrue(testEvaluate(
                "LCM[18, 24]", "72"
        ));
    }

    private boolean testEvaluate(String expression, String expectedResult) {
        return mathEclipseFacade.evaluate(expression).getExpressionEvaluated().contains(expectedResult);
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
        return mathEclipseFacade.calculate(expression, CALCULATION_DECIMALS).getExpressionEvaluated().contains(expectedResult);
    }

    @Test
    @DisplayName("Draw: expresiones para plot")
    void testDraw() {
        assertTrue(testDraw(
                "Sin[x]", "-Pi", "Pi"
        ));
        assertTrue(testDraw(
                "x^2", "-5", "5"
        ));
        assertTrue(testDraw(
                "Tan[x]", "-Pi/2", "Pi/2"
        ));
    }

    private boolean testDraw(String expression, String origin, String bound) {
        return mathEclipseFacade.draw(expression, DRAW_VARIABLE, origin, bound).getExpressionEvaluated().contains()
    }

    @Test
    @DisplayName("Validate: expresiones inválidas")
    void testValidateInvalid() {
        assertTrue(mathEclipseFacade.validate("xx^2 + 1").startsWith(MathEclipseExpressionValidator.ERROR_SYMBOL));
        assertTrue(mathEclipseFacade.validate("Plot[Sin[x], {x, -1, 1}]").startsWith(MathEclipseExpressionValidator.ERROR_SYMBOL));
        assertTrue(mathEclipseFacade.validate("Integrate[Sin[x]").startsWith(MathEclipseExpressionValidator.ERROR_SYMBOL));
    }



    @Test
    @DisplayName("Evaluate: expresiones inválidas")
    void testEvaluateInvalid() {
        assertTrue(mathEclipseFacade.evaluate("velocity + 3").startsWith(MathEclipseExpressionValidator.ERROR_SYMBOL));
        assertTrue(mathEclipseFacade.evaluate("Minimize[x^2 + 1, x]").startsWith(MathEclipseExpressionValidator.ERROR_SYMBOL));
        assertTrue(mathEclipseFacade.evaluate("Solve[x^2 == 1, ]").startsWith(MathEclipseExpressionValidator.ERROR_SYMBOL));
    }

    @Test
    @DisplayName("Calculate: expresiones inválidas")
    void testCalculateInvalid() {
        assertTrue(mathEclipseFacade.calculate("Plot[Sin[x]]", 5).startsWith(MathEclipseExpressionValidator.ERROR_SYMBOL));
        assertTrue(mathEclipseFacade.calculate("x^^2", 10).startsWith(MathEclipseExpressionValidator.ERROR_SYMBOL));
        assertTrue(mathEclipseFacade.calculate("Sqrt[longvar]", 8).startsWith(MathEclipseExpressionValidator.ERROR_SYMBOL));
    }

    @Test
    @DisplayName("Draw: expresiones inválidas")
    void testDrawInvalid() {
        assertTrue(mathEclipseFacade.draw("x^2 + speed", "x", "-5", "5").startsWith(MathEclipseExpressionValidator.ERROR_SYMBOL));
        assertTrue(mathEclipseFacade.draw("Sum[1/x, {x, 1, 10}]", "x", "1", "10").startsWith(MathEclipseExpressionValidator.ERROR_SYMBOL));
        assertTrue(mathEclipseFacade.draw("D[x^2,, x]", "x", "-2", "2").startsWith(MathEclipseExpressionValidator.ERROR_SYMBOL));
    }

    private <T extends Throwable> boolean testThrows(Class<T> exceptionClass, Consumer<String> operation) {
        try {
            operation.accept();
            return false;
        } catch (Throwable e) {
            return exceptionClass.isInstance(e);
        }
    }

    @Test
    @DisplayName("Stop Request: interrupt ongoing evaluation")
    void testStopRequest() {
        Thread evaluationThread = new Thread(() -> {
            try {
                String impossibleExpression = "Integrate[log(sin(x^2 + cos(x))) / (1 + x^6), x]";
                String result = mathEclipseFacade.evaluate(impossibleExpression);
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
