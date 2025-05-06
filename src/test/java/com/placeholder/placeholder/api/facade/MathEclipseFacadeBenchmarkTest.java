package com.placeholder.placeholder.api.facade;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.form.tex.TeXFormFactory;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

class MathEclipseFacadeBenchmarkTest {

    private static final int ITERATIONS = 1000;
    private static final boolean PRINT_RESULTS = true;
    private MathEclipseFacade facade;

    @BeforeEach
    void setUp() {
        EvalEngine engine = new EvalEngine("benchmark", 100, null, true);
        EvalUtilities evaluator = new EvalUtilities(engine, false, false);
        facade = new MathEclipseFacade(evaluator, new MathExpressionValidator(), new TeXFormFactory(), false);
    }

    // Expand & Simplify

    /** */
    @Test void benchmarkExpand1() { benchmark(() -> facade.evaluate("Expand[(x + 1)^10]")); }

    /** */
    @Test void benchmarkExpand2() { benchmark(() -> facade.evaluate("Expand[(x - y)^15]")); }

    /** */
    @Test void benchmarkSimplify1() { benchmark(() -> facade.evaluate("Simplify[(x^2 - 1)/(x - 1)]")); }

    /** */
    @Test void benchmarkSimplify2() { benchmark(() -> facade.evaluate("Simplify[Sin[x]^2 + Cos[x]^2]")); }

    // Derivatives

    /** */
    @Test void benchmarkDerivative1() { benchmark(() -> facade.evaluate("D[x^5 + 2*x^3 + x, x]")); }

    /** */
    @Test void benchmarkDerivative2() { benchmark(() -> facade.evaluate("D[Sin[x]*Cos[x], x]")); }

    /** */
    @Test void benchmarkHighOrderDerivative() { benchmark(() -> facade.evaluate("D[x^10 + 3*x^5, {x, 5}]")); }

    // Integrates

    /** */
    @Test void benchmarkIntegralPolynomial() { benchmark(() -> facade.evaluate("Integrate[x^2 + 3*x + 2, x]")); }

    /** */
    @Test void benchmarkIntegralTrig() { benchmark(() -> facade.evaluate("Integrate[Sin[x], x]")); }

    /** */
    @Test void benchmarkIntegralExp() { benchmark(() -> facade.evaluate("Integrate[Exp[x], x]")); }

    /** */
    @Test void benchmarkIntegralLog() { benchmark(() -> facade.evaluate("Integrate[Log[x], x]")); }

    /** */
    @Test void benchmarkIntegralRational() { benchmark(() -> facade.evaluate("Integrate[1/(x^2 + 1), x]")); }

    // Limits

    /** */
    @Test void benchmarkLimit1() { benchmark(() -> facade.evaluate("Limit[(1 + 1/x)^x, x -> Infinity]")); }

    /** */
    @Test void benchmarkLimit2() { benchmark(() -> facade.evaluate("Limit[Sin[x]/x, x -> 0]")); }

    // Equations

    /** */
    @Test void benchmarkSolve1() { benchmark(() -> facade.evaluate("Solve[x^2 == 1, x]")); }

    /** */
    @Test void benchmarkSolve2() { benchmark(() -> facade.evaluate("Solve[{x + y == 2, x - y == 0}, {x, y}]")); }

    // Calculations

    /** */
    @Test void benchmarkCalcE() { benchmark(10, () -> facade.calculate("E", 50)); }

    /** */
    @Test void benchmarkCalcPi() { benchmark(10, () -> facade.calculate("Pi", 50)); }

    /** */
    @Test void benchmarkCalcLog() { benchmark(10, () -> facade.calculate("Log[10]", 50)); }

    /** */
    @Test void benchmarkCalcSqrt2() { benchmark(10, () -> facade.calculate("Sqrt[2]", 50)); }

    // Matrix

    /** */
    @Test void benchmarkMatrixInverse() { benchmark(50, () -> facade.evaluate("Inverse[{{1, 2}, {3, 4}}]")); }

    /** */
    @Test void benchmarkMatrixTranspose() { benchmark(50, () -> facade.evaluate("Transpose[{{1, 2}, {3, 4}}]")); }

    /** */
    @Test void benchmarkMatrixEigen() { benchmark(50, () -> facade.evaluate("Eigenvalues[{{2, 1}, {1, 2}}]")); }

    // Boolean Functions

    /** */
    @Test void benchmarkPrimeQ() { benchmark(10, () -> facade.evaluate("PrimeQ[97]")); }

    /** */
    @Test void benchmarkGCD() { benchmark(10, () -> facade.evaluate("GCD[252, 105]")); }

    /** */
    @Test void benchmarkLCM() { benchmark(10, () -> facade.evaluate("LCM[252, 105]")); }

    // Transcendental Native Functions

    /** */
    @Test void benchmarkGamma() { benchmark(() -> facade.evaluate("Gamma[5]")); }

    /** */
    @Test void benchmarkZeta() { benchmark(() -> facade.evaluate("Zeta[2]")); }

    /** */
    @Test void benchmarkErf() { benchmark(() -> facade.evaluate("Erf[1]")); }

    // Draw

    /** */
    @Test void benchmarkDrawSin() { benchmark(10, () -> facade.draw("Sin[x]", "x", "-Pi", "Pi")); }

    /** */
    @Test void benchmarkDrawQuadratic() { benchmark(10, () -> facade.draw("x^2", "x", "-10", "10")); }

    /** */
    @Test void benchmarkDrawExp() { benchmark(10, () -> facade.draw("Exp[x]", "x", "-2", "2")); }

    /** */
    @Test void benchmarkDrawTrigCombo() { benchmark(10, () -> facade.draw("Sin[x]*Cos[x]", "x", "-Pi", "Pi")); }

    // DIFFICULT TO PROCESS

    /** 23754 ms, ~23 seg */
    @Test void benchmarkIntegralRationalRootIrreducible() { benchmark(0.001, () -> facade.evaluate("Integrate[(1 - x^4)/((1 + x^2 + x^4)*(1 + x^4)^(1/4)), x]")); }

    /** 262225 ms, ~262 seg, ~4,3 min */
    @Test void benchmarkIntegralRationalArctanQuadratic() { benchmark(0.001, () -> facade.evaluate("Integrate[(x^2 - 1)/((x^4 + 3x^2 + 1)*(arctan(x^2 + 1/x))), x]")); }

    /** 5287 ms, ~5,2 seg */
    @Test void benchmarkIntegralLogComplexTrinomial() { benchmark(0.001, () -> facade.evaluate("Integrate[(log(x^2 + 3*x + 2)) / sqrt(1 + x^6 + x^4), x]")); }

    /** */
    @Test void benchmarkIntegralRecursiveTrigLog() { benchmark(0.001, () -> facade.evaluate("Integrate[log(sin(x^2 + cos(x))) / (1 + x^6), x]")); }

    /** 10706 ms, ~10 seg */
    @Test void benchmarkIntegralExponentialRational() { benchmark(0.001, () -> facade.evaluate("Integrate[(x^5 * exp(x^2)) / (1 + x^4), x]")); }

    /** 16978 ms, ~17 seg */
    @Test void benchmarkIntegralRationalInverseTan() { benchmark(0.001, () -> facade.evaluate("Integrate[(x^4 + 3)/(1 + arctan(x)^2), x]")); }

    /** 2449 ms, ~2,5 seg */
    @Test void benchmarkIntegralZetaFunction() { benchmark(0.001, () -> facade.evaluate("Integrate[Zeta(x^2 + 1), x]")); }

    /** 5926 ms, ~6 seg */
    @Test void benchmarkIntegralAbsRootRational() { benchmark(0.001, () -> facade.evaluate("Integrate[(abs(x^3 - x + 1)) / (1 + x^6)^(1/3), x]")); }

    /** 36499 ms, ~36 seg */
    @Test void benchmarkIntegralOscillatoryComposite() { benchmark(0.001, () -> facade.evaluate("Integrate[exp(x) * sin(x^3) / (1 + x^2), x]")); }

    /** 4117 ms, ~4 seg */
    @Test void benchmarkIntegralEllipticLike() { benchmark(0.001, () -> facade.evaluate("Integrate[(x^2 * sqrt(1 - x^2)) / (1 + x^4), x]")); }

    private <T> void benchmark(Supplier<T> task) {
        benchmarkInternal(ITERATIONS, task);
    }

    private <T> void benchmark(double iterationsMultiplier, Supplier<T> task) {
        long newIterations = (long) ((double) ITERATIONS * iterationsMultiplier);
        if (newIterations <= 0) newIterations = ITERATIONS;
        benchmarkInternal(newIterations, task);
    }

    private <T> void benchmarkInternal(long iterations, Supplier<T> task) {
        long start = System.nanoTime();

        for (long i = 0; i < iterations; i++) {
            task.get();
        }

        long end = System.nanoTime();

        if (PRINT_RESULTS) System.out.println(task.get());

        long durationNanos = end - start;
        double avgPerIterationMillis = (double) durationNanos / iterations / 1_000_000.0;

        System.out.println("Iterations: " + iterations);
        System.out.printf("Total time: %d ms%n", TimeUnit.NANOSECONDS.toMillis(durationNanos));
        System.out.printf("Average time per iteration: %.6f ms%n", avgPerIterationMillis);
    }
}
