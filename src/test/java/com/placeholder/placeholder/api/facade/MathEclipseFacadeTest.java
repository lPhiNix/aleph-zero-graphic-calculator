package com.placeholder.placeholder.api.facade;

import com.placeholder.placeholder.api.math.facade.symja.MathEclipseConfig;
import com.placeholder.placeholder.api.math.facade.symja.MathEclipseFacade;
import com.placeholder.placeholder.api.math.facade.symja.exceptions.MathEclipseGrammaticalException;
import com.placeholder.placeholder.api.math.facade.symja.exceptions.MathEclipseSemanticException;
import com.placeholder.placeholder.api.math.facade.symja.exceptions.MathEclipseSyntaxException;
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
    @DisplayName("Validate: valid expressions")
    void testValidateValidExpressions() {
        assertEquals(
                "x^2+2*x+1",
                testValidate("x^2 + 2*x + 1")
        );
        assertEquals(
                "Integrate(Sin(x),x)",
                testValidate("Integrate[Sin[x], x]")
        );
        assertEquals(
                "D(x^3,x)",
                testValidate("D[x^3, x]")
        );
        assertEquals(
                "Solve(x^2==1,x)",
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
                "x^2", "-5", "5"
        ));
        assertTrue(testDraw(
                "Tan[x]", "-Pi/2", "Pi/2"
        ));
    }

    private boolean testDraw(String expression, String origin, String bound) {
        return rawTestDraw(expression, origin, bound).contains(GRAPHICS_SYMBOL);
    }

    private String rawTestDraw(String expression, String origin, String bound) {
        return mathEclipseFacade.draw(expression, DRAW_VARIABLE, origin, bound).getExpressionEvaluated();
    }

    @Test
    @DisplayName("Validate: expresiones inválidas")
    void testValidateInvalid() {
        assertThrows(
                MathEclipseGrammaticalException.class,
                () -> testValidate("xx^2 + 1")
        );
        assertThrows(
                MathEclipseSemanticException.class,
                () -> testValidate("Plot[Sin[x], {x, -1, 1}]")
        );
        assertThrows(
                MathEclipseSyntaxException.class,
                () -> testValidate("Integrate[Sin[x]")
        );
    }

    @Test
    @DisplayName("Evaluate: expresiones inválidas")
    void testEvaluateInvalid() {
        assertThrows(
                MathEclipseGrammaticalException.class,
                () -> rawTestEvaluate("velocity + 3")
        );
        assertThrows(
                MathEclipseSemanticException.class,
                () -> rawTestEvaluate("Minimize[x^2 + 1, x]")
        );
        assertThrows(
                MathEclipseSyntaxException.class,
                () -> rawTestEvaluate("Solve[x^2 = 1, ")
        );
    }

    @Test
    @DisplayName("Calculate: expresiones inválidas")
    void testCalculateInvalid() {
        assertThrows(
                MathEclipseSemanticException.class,
                () -> rawTestCalculate("Plot[Sin[x]]")
        );
        assertThrows(
                MathEclipseSyntaxException.class,
                () -> rawTestCalculate("x^^2")
        );
        assertThrows(
                MathEclipseGrammaticalException.class,
                () -> rawTestCalculate("Sqrt[longvar]")
        );
    }

    @Test
    @DisplayName("Draw: expresiones inválidas")
    void testDrawInvalid() {
        assertThrows(
                MathEclipseGrammaticalException.class,//TODO
                () -> rawTestDraw("x^2 + speed", "-5", "5")
        );

        assertThrows(
                MathEclipseSemanticException.class,
                () -> rawTestDraw("Sum[1/x, {x, 1, 10}]", "1", "10")
        );

        assertThrows(
                MathEclipseSyntaxException.class,
                () -> rawTestDraw("D[x^2,, x]", "-2", "2")
        );
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
