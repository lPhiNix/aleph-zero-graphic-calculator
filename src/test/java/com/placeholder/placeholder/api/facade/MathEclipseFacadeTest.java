package com.placeholder.placeholder.api.facade;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.form.tex.TeXFormFactory;

import static org.junit.jupiter.api.Assertions.*;

class MathEclipseFacadeTest {

    private MathEclipseFacade facade;

    @BeforeEach
    void setUp() {
        EvalEngine engine = new EvalEngine("test", 100, null, true);
        EvalUtilities evaluator = new EvalUtilities(engine, false, false);
        facade = new MathEclipseFacade(evaluator, new MathExpressionValidator(), new TeXFormFactory());
    }

    @Test
    @DisplayName("Validate: valid expressions")
    void testValidate_ValidExpressions() {
        assertEquals("x^2+2*x+1", facade.validate("x^2 + 2*x + 1"));
        assertEquals("Integrate[Sin[x],x]", facade.validate("Integrate[Sin[x], x]"));
        assertEquals("D[x^3,x]", facade.validate("D[x^3, x]"));
        assertEquals("Solve[x^2==1,x]", facade.validate("Solve[x^2 == 1, x]"));
    }

    @Test
    @DisplayName("Evaluate: valid expressions")
    void testEvaluate_ValidExpressions() {
        assertTrue(facade.evaluate("Expand[(x + 1)^2]").contains("1+2*x+x^2"));
        assertTrue(facade.evaluate("Simplify[(x^2 - 1)/(x - 1)]").contains("1+x"));
        assertTrue(facade.evaluate("Solve[x^2 - 1 == 0, x]").contains("x->1"));
        assertTrue(facade.evaluate("Integrate[x^2, x]").contains("x^3/3"));
        assertTrue(facade.evaluate("D[x^3 + 2*x^2 + x, {x, 2}]").contains("4+6*x"));
        assertTrue(facade.evaluate("Limit[(1+1/x)^x, x -> Infinity]").contains("E"));
        assertTrue(facade.evaluate("Inverse[{{1, 2}, {3, 4}}]").contains("{{-2,1},\n {3/2,-1/2}}"));
        assertTrue(facade.evaluate("Transpose[{{1, 2}, {3, 4}}]").contains("{{1,3},\n {2,4}}"));
        assertTrue(facade.evaluate("Eigenvalues[{{2, 1}, {1, 2}}]").contains("{3,1}"));
        assertTrue(facade.evaluate("PrimeQ[7]").toLowerCase().contains("true"));
        assertTrue(facade.evaluate("GCD[18, 24]").contains("6"));
        assertTrue(facade.evaluate("LCM[18, 24]").contains("72"));
    }

    @Test
    @DisplayName("Calculate: numeric expressions")
    void testCalculate() {
        assertTrue(facade.calculate("E", 17).contains("2.718281828459045"));
        assertTrue(facade.calculate("Sqrt[2]", 17).contains("1.414213562373095"));
        assertTrue(facade.calculate("Log[10]", 17).contains("2.3025850929940456"));
    }

    @Test
    @DisplayName("Draw: expresiones para plot")
    void testDraw() {
        assertTrue(facade.draw("Sin[x]", "x", "-Pi", "Pi").contains("Graphics"));
        assertTrue(facade.draw("x^2", "x", "-5", "5").contains("Graphics"));
        assertTrue(facade.draw("Tan[x]", "x", "-Pi/2", "Pi/2").contains("Graphics"));
    }

    @Test
    @DisplayName("Validate: expresiones inválidas")
    void testValidate_Invalid() {
        assertTrue(facade.validate("xx^2 + 1").startsWith(MathExpressionValidator.ERROR_SYMBOL));
        assertTrue(facade.validate("Plot[Sin[x], {x, -1, 1}]").startsWith(MathExpressionValidator.ERROR_SYMBOL));
        assertTrue(facade.validate("Integrate[Sin[x]").startsWith(MathExpressionValidator.ERROR_SYMBOL));
    }

    @Test
    @DisplayName("Evaluate: expresiones inválidas")
    void testEvaluate_Invalid() {
        assertTrue(facade.evaluate("velocity + 3").startsWith(MathExpressionValidator.ERROR_SYMBOL));
        assertTrue(facade.evaluate("Minimize[x^2 + 1, x]").startsWith(MathExpressionValidator.ERROR_SYMBOL));
        assertTrue(facade.evaluate("Solve[x^2 == 1, ]").startsWith(MathExpressionValidator.ERROR_SYMBOL));
    }

    @Test
    @DisplayName("Calculate: expresiones inválidas")
    void testCalculate_Invalid() {
        assertTrue(facade.calculate("Plot[Sin[x]]", 5).startsWith(MathExpressionValidator.ERROR_SYMBOL));
        assertTrue(facade.calculate("x^^2", 10).startsWith(MathExpressionValidator.ERROR_SYMBOL));
        assertTrue(facade.calculate("Sqrt[longvar]", 8).startsWith(MathExpressionValidator.ERROR_SYMBOL));
    }

    @Test
    @DisplayName("Draw: expresiones inválidas")
    void testDraw_Invalid() {
        assertTrue(facade.draw("x^2 + speed", "x", "-5", "5").startsWith(MathExpressionValidator.ERROR_SYMBOL));
        assertTrue(facade.draw("Sum[1/x, {x, 1, 10}]", "x", "1", "10").startsWith(MathExpressionValidator.ERROR_SYMBOL));
        assertTrue(facade.draw("D[x^2,, x]", "x", "-2", "2").startsWith(MathExpressionValidator.ERROR_SYMBOL));
    }
}
