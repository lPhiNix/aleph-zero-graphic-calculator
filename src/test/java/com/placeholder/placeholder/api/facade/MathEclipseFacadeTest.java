package com.placeholder.placeholder.api.facade;

import com.placeholder.placeholder.api.math.facade.symja.MathEclipseConfig;
import com.placeholder.placeholder.api.math.facade.symja.MathEclipseExpressionValidator;
import com.placeholder.placeholder.api.math.facade.symja.MathEclipseFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.form.tex.TeXFormFactory;

import static org.junit.jupiter.api.Assertions.*;

class MathEclipseFacadeTest {

    private MathEclipseFacade mathEclipseFacade;

    @BeforeEach
    void setUp() {
        EvalUtilities evaluator = MathEclipseConfig.buildEvalUtilities("test");
        TeXFormFactory laTeXParser = MathEclipseConfig.buildTeXFormFactory();

        mathEclipseFacade = new MathEclipseFacade(
                evaluator,
                new MathEclipseExpressionValidator(),
                new MathEclipseExpressionFactory(),
                laTeXParser
        );
    }

    @Test
    @DisplayName("Validate: valid expressions")
    void testValidate_ValidExpressions() {
        assertEquals("x^2+2*x+1", mathEclipseFacade.validate("x^2 + 2*x + 1"));
        assertEquals("Integrate[Sin[x],x]", mathEclipseFacade.validate("Integrate[Sin[x], x]"));
        assertEquals("D[x^3,x]", mathEclipseFacade.validate("D[x^3, x]"));
        assertEquals("Solve[x^2==1,x]", mathEclipseFacade.validate("Solve[x^2 == 1, x]"));
    }

    @Test
    @DisplayName("Evaluate: valid expressions")
    void testEvaluate_ValidExpressions() {
        assertTrue(mathEclipseFacade.evaluate("Expand[(x + 1)^2]").contains("1+2*x+x^2"));
        assertTrue(mathEclipseFacade.evaluate("Simplify[(x^2 - 1)/(x - 1)]").contains("1+x"));
        assertTrue(mathEclipseFacade.evaluate("Solve[x^2 - 1 == 0, x]").contains("x->1"));
        assertTrue(mathEclipseFacade.evaluate("Integrate[x^2, x]").contains("x^3/3"));
        assertTrue(mathEclipseFacade.evaluate("D[x^3 + 2*x^2 + x, {x, 2}]").contains("4+6*x"));
        assertTrue(mathEclipseFacade.evaluate("Limit[(1+1/x)^x, x -> Infinity]").contains("E"));
        assertTrue(mathEclipseFacade.evaluate("Inverse[{{1, 2}, {3, 4}}]").contains("{{-2,1},\n {3/2,-1/2}}"));
        assertTrue(mathEclipseFacade.evaluate("Transpose[{{1, 2}, {3, 4}}]").contains("{{1,3},\n {2,4}}"));
        assertTrue(mathEclipseFacade.evaluate("Eigenvalues[{{2, 1}, {1, 2}}]").contains("{3,1}"));
        assertTrue(mathEclipseFacade.evaluate("PrimeQ[7]").toLowerCase().contains("true"));
        assertTrue(mathEclipseFacade.evaluate("GCD[18, 24]").contains("6"));
        assertTrue(mathEclipseFacade.evaluate("LCM[18, 24]").contains("72"));
    }

    @Test
    @DisplayName("Calculate: numeric expressions")
    void testCalculate() {
        assertTrue(mathEclipseFacade.calculate("E", 17).contains("2.718281828459045"));
        assertTrue(mathEclipseFacade.calculate("Sqrt[2]", 17).contains("1.414213562373095"));
        assertTrue(mathEclipseFacade.calculate("Log[10]", 17).contains("2.3025850929940456"));
    }

    @Test
    @DisplayName("Draw: expresiones para plot")
    void testDraw() {
        assertTrue(mathEclipseFacade.draw("Sin[x]", "x", "-Pi", "Pi").contains("Graphics"));
        assertTrue(mathEclipseFacade.draw("x^2", "x", "-5", "5").contains("Graphics"));
        assertTrue(mathEclipseFacade.draw("Tan[x]", "x", "-Pi/2", "Pi/2").contains("Graphics"));
    }

    @Test
    @DisplayName("Validate: expresiones inválidas")
    void testValidate_Invalid() {
        assertTrue(mathEclipseFacade.validate("xx^2 + 1").startsWith(MathEclipseExpressionValidator.ERROR_SYMBOL));
        assertTrue(mathEclipseFacade.validate("Plot[Sin[x], {x, -1, 1}]").startsWith(MathEclipseExpressionValidator.ERROR_SYMBOL));
        assertTrue(mathEclipseFacade.validate("Integrate[Sin[x]").startsWith(MathEclipseExpressionValidator.ERROR_SYMBOL));
    }

    @Test
    @DisplayName("Evaluate: expresiones inválidas")
    void testEvaluate_Invalid() {
        assertTrue(mathEclipseFacade.evaluate("velocity + 3").startsWith(MathEclipseExpressionValidator.ERROR_SYMBOL));
        assertTrue(mathEclipseFacade.evaluate("Minimize[x^2 + 1, x]").startsWith(MathEclipseExpressionValidator.ERROR_SYMBOL));
        assertTrue(mathEclipseFacade.evaluate("Solve[x^2 == 1, ]").startsWith(MathEclipseExpressionValidator.ERROR_SYMBOL));
    }

    @Test
    @DisplayName("Calculate: expresiones inválidas")
    void testCalculate_Invalid() {
        assertTrue(mathEclipseFacade.calculate("Plot[Sin[x]]", 5).startsWith(MathEclipseExpressionValidator.ERROR_SYMBOL));
        assertTrue(mathEclipseFacade.calculate("x^^2", 10).startsWith(MathEclipseExpressionValidator.ERROR_SYMBOL));
        assertTrue(mathEclipseFacade.calculate("Sqrt[longvar]", 8).startsWith(MathEclipseExpressionValidator.ERROR_SYMBOL));
    }

    @Test
    @DisplayName("Draw: expresiones inválidas")
    void testDraw_Invalid() {
        assertTrue(mathEclipseFacade.draw("x^2 + speed", "x", "-5", "5").startsWith(MathEclipseExpressionValidator.ERROR_SYMBOL));
        assertTrue(mathEclipseFacade.draw("Sum[1/x, {x, 1, 10}]", "x", "1", "10").startsWith(MathEclipseExpressionValidator.ERROR_SYMBOL));
        assertTrue(mathEclipseFacade.draw("D[x^2,, x]", "x", "-2", "2").startsWith(MathEclipseExpressionValidator.ERROR_SYMBOL));
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
