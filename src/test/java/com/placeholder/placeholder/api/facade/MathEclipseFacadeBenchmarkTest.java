package com.placeholder.placeholder.api.facade;

import com.placeholder.placeholder.api.math.facade.symja.MathEclipseConfig;
import com.placeholder.placeholder.api.math.facade.symja.MathEclipseFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

class MathEclipseFacadeBenchmarkTest {

    private static final int BASE_ITERATIONS = 1000;
    private static final boolean PRINT_RESULTS = true;

    private MathEclipseFacade mathEclipseFacade;

    @BeforeEach
    void setUp() {
        mathEclipseFacade = MathEclipseConfig.buildMathEclipseFacade();
    }

    // BENCHMARK (ms and seg per ONE iteration)

    // Expand & Simplify

    /** 1,062834 ms, ~0,001 seg */
    @DisplayName("evaluate: Expand[(x + 1)^10]")
    @Test void benchmarkExpand1() { benchmark(() -> mathEclipseFacade.evaluate("Expand[(x + 1)^10]")); }

    /** 1,585798 ms, ~0,00158 seg */
    @DisplayName("evaluate: Expand[(x - y)^15]")
    @Test void benchmarkExpand2() { benchmark(() -> mathEclipseFacade.evaluate("Expand[(x - y)^15]")); }

    /** 0,962512 ms, ~0,00096 seg */
    @DisplayName("evaluate: Simplify[(x^2 - 1)/(x - 1)]")
    @Test void benchmarkSimplify1() { benchmark(() -> mathEclipseFacade.evaluate("Simplify[(x^2 - 1)/(x - 1)]")); }

    /** 0,557924 ms, ~0,00055 seg */
    @DisplayName("evaluate: Simplify[Sin[x]^2 + Cos[x]^2]")
    @Test void benchmarkSimplify2() { benchmark(() -> mathEclipseFacade.evaluate("Simplify[Sin[x]^2 + Cos[x]^2]")); }

    // Derivatives

    /** 1,185651 ms, ~0,0012 seg */
    @DisplayName("evaluate: D[x^5 + 2*x^3 + x, x]")
    @Test void benchmarkDerivative1() { benchmark(() -> mathEclipseFacade.evaluate("D[x^5 + 2*x^3 + x, x]")); }

    /** 1,145509 ms, ~0,0011 seg */
    @DisplayName("evaluate: D[Sin[x]*Cos[x], x]")
    @Test void benchmarkDerivative2() { benchmark(() -> mathEclipseFacade.evaluate("D[Sin[x]*Cos[x], x]")); }

    /** 2,111846 ms, ~0,0021 seg */
    @DisplayName("evaluate: D[x^10 + 3*x^5, {x, 5}]")
    @Test void benchmarkHighOrderDerivative() { benchmark(() -> mathEclipseFacade.evaluate("D[x^10 + 3*x^5, {x, 5}]")); }

    // Integrates

    /** 53,627914 ms, 0,053 seg */
    @DisplayName("evaluate: Integrate[x^2 + 3*x + 2, x]")
    @Test void benchmarkIntegralPolynomial() { benchmark(() -> mathEclipseFacade.evaluate("Integrate[x^2 + 3*x + 2, x]")); }

    /** 25,676889 ms, ~0,025 seg */
    @DisplayName("evaluate: Integrate[Sin[x], x]")
    @Test void benchmarkIntegralTrig() { benchmark(() -> mathEclipseFacade.evaluate("Integrate[Sin[x], x]")); }

    /** 12,256350 ms, ~0,012 seg */
    @DisplayName("evaluate: Integrate[Exp[x], x]")
    @Test void benchmarkIntegralExp() { benchmark(() -> mathEclipseFacade.evaluate("Integrate[Exp[x], x]")); }

    /** 22,873190 ms, ~0,022 seg */
    @DisplayName("evaluate: Integrate[Log[x], x]")
    @Test void benchmarkIntegralLog() { benchmark(() -> mathEclipseFacade.evaluate("Integrate[Log[x], x]")); }

    /** 20,347167 ms, ~0,02 seg */
    @DisplayName("evaluate: Integrate[1/(x^2 + 1), x]")
    @Test void benchmarkIntegralRational() { benchmark(() -> mathEclipseFacade.evaluate("Integrate[1/(x^2 + 1), x]")); }

    // Limits

    /** 0,818747 ms, ~0,0008 seg */
    @DisplayName("evaluate: Limit[(1 + 1/x)^x, x -> Infinity]")
    @Test void benchmarkLimit1() { benchmark(() -> mathEclipseFacade.evaluate("Limit[(1 + 1/x)^x, x -> Infinity]")); }

    /** 1,308160 ms, ~0,0013 seg */
    @DisplayName("evaluate: Limit[Sin[x]/x, x -> 0]")
    @Test void benchmarkLimit2() { benchmark(() -> mathEclipseFacade.evaluate("Limit[Sin[x]/x, x -> 0]")); }

    // Equations

    /** 2,553616 ms, 0,0025 seg */
    @DisplayName("evaluate: Solve[x^2 == 1, x]")
    @Test void benchmarkSolve1() { benchmark(() -> mathEclipseFacade.evaluate("Solve[x^2 == 1, x]")); }

    /** 3,784876 ms, ~0,0038 seg */
    @DisplayName("evaluate: Solve[{x + y == 2, x - y == 0}, {x, y}]")
    @Test void benchmarkSolve2() { benchmark(() -> mathEclipseFacade.evaluate("Solve[{x + y == 2, x - y == 0}, {x, y}]")); }

    // Calculations

    /** 2,064417 ms , ~0,002 seg */
    @DisplayName("calculate: E")
    @Test void benchmarkCalcE() { benchmark(10, () -> mathEclipseFacade.calculate("E", 50)); }

    /** 0,063505 ms, ~0,000064 seg */
    @DisplayName("calculate: Pi")
    @Test void benchmarkCalcPi() { benchmark(10, () -> mathEclipseFacade.calculate("Pi", 50)); }

    /** 0,738622 ms, ~0,00074 seg */
    @DisplayName("calculate: Log[10]")
    @Test void benchmarkCalcLog() { benchmark(10, () -> mathEclipseFacade.calculate("Log[10]", 50)); }

    /** 2,892125 ms, ~0,003 seg */
    @DisplayName("calculate: Sqrt[2]")
    @Test void benchmarkCalcSqrt2() { benchmark(10, () -> mathEclipseFacade.calculate("Sqrt[2]", 50)); }

    // Matrix

    /** 0,051541 ms, ~0,00005 seg */
    @DisplayName("evaluate: Inverse[{{1, 2}, {3, 4}}]")
    @Test void benchmarkMatrixInverse() { benchmark(50, () -> mathEclipseFacade.evaluate("Inverse[{{1, 2}, {3, 4}}]")); }

    /** 0,039768 ms, ~0,00004 seg */
    @DisplayName("evaluate: Transpose[{{1, 2}, {3, 4}}]")
    @Test void benchmarkMatrixTranspose() { benchmark(50, () -> mathEclipseFacade.evaluate("Transpose[{{1, 2}, {3, 4}}]")); }

    /** 0,127205 ms, ~0,00012 seg */
    @DisplayName("evaluate: Eigenvalues[{{2, 1}, {1, 2}}]")
    @Test void benchmarkMatrixEigen() { benchmark(50, () -> mathEclipseFacade.evaluate("Eigenvalues[{{2, 1}, {1, 2}}]")); }

    // Boolean Functions

    /** 0,065142 ms, ~0,00007 seg */
    @DisplayName("evaluate: GCD[252, 105]")
    @Test void benchmarkGCD() { benchmark(10, () -> mathEclipseFacade.evaluate("GCD[252, 105]")); }

    /** 0,059395 ms, ~0,00006 seg */
    @DisplayName("evaluate: LCM[252, 105]")
    @Test void benchmarkLCM() { benchmark(10, () -> mathEclipseFacade.evaluate("LCM[252, 105]")); }

    // Transcendental Native Functions

    /**0,254754 ms, ~0,00025 seg */
    @DisplayName("evaluate: Gamma[5]")
    @Test void benchmarkGamma() { benchmark(() -> mathEclipseFacade.evaluate("Gamma[5]")); }

    /** 0,740738 ms, ~0,0007 seg */
    @DisplayName("evaluate: Zeta[2]")
    @Test void benchmarkZeta() { benchmark(() -> mathEclipseFacade.evaluate("Zeta[2]")); }

    /** 0,201064 ms, ~0,0002 seg */
    @DisplayName("evaluate: Erf[1]")
    @Test void benchmarkErf() { benchmark(() -> mathEclipseFacade.evaluate("Erf[1]")); }

    // Draw

    /** 0,527401 ms, ~0,00052 seg */
    @DisplayName("draw: Sin[x]")
    @Test void benchmarkDrawSin() { benchmark(10, () -> mathEclipseFacade.draw("Sin[x]", "x", "-Pi", "Pi")); }

    /** 1,882207 ms, ~0,0019 seg */
    @DisplayName("draw: x^2")
    @Test void benchmarkDrawQuadratic() { benchmark(10, () -> mathEclipseFacade.draw("x^2", "x", "-10", "10")); }

    /** 0,654370 ms, ~0,00065 seg */
    @DisplayName("draw: Exp[x]")
    @Test void benchmarkDrawExp() { benchmark(10, () -> mathEclipseFacade.draw("Exp[x]", "x", "-2", "2")); }

    /** 0,970677 ms, ~0,00097 seg */
    @DisplayName("draw: Sin[x]*Cos[x]")
    @Test void benchmarkDrawTrigCombo() { benchmark(10, () -> mathEclipseFacade.draw("Sin[x]*Cos[x]", "x", "-Pi", "Pi")); }

    @DisplayName("draw: Sin[x]")
    @Test void benchmarkDrawSinHARD() { benchmark(0.01, () -> mathEclipseFacade.draw("Sin[x]", "x", "-1000000", "1000000")); }


    // DIFFICULT TO PROCESS (> ~1 seg per ONE iteration)

    /** 23754 ms, ~23 seg */
    @DisplayName("evaluate: Integrate[(1 - x^4)/((1 + x^2 + x^4)*(1 + x^4)^(1/4)), x]")
    @Test void benchmarkIntegralRationalRootIrreducible() { benchmark(0.001, () -> mathEclipseFacade.evaluate("Integrate[(1 - x^4)/((1 + x^2 + x^4)*(1 + x^4)^(1/4)), x]")); }

    /** 262225 ms, ~262 seg, ~4,3 min */
    @DisplayName("evaluate: Integrate[(x^2 - 1)/((x^4 + 3x^2 + 1)*(arctan(x^2 + 1/x))), x]")
    @Test void benchmarkIntegralRationalArctanQuadratic() { benchmark(0.001, () -> mathEclipseFacade.evaluate("Integrate[(x^2 - 1)/((x^4 + 3x^2 + 1)*(arctan(x^2 + 1/x))), x]")); }

    /** 5287 ms, ~5,2 seg */
    @DisplayName("evaluate: Integrate[(log(x^2 + 3*x + 2)) / sqrt(1 + x^6 + x^4), x]")
    @Test void benchmarkIntegralLogComplexTrinomial() { benchmark(0.001, () -> mathEclipseFacade.evaluate("Integrate[(log(x^2 + 3*x + 2)) / sqrt(1 + x^6 + x^4), x]")); }

    /** INFINITE (DOESN'T WORK) */
    @DisplayName("evaluate: Integrate[log(sin(x^2 + cos(x))) / (1 + x^6), x]")
    @Test void benchmarkIntegralRecursiveTrigLog() { benchmark(0.001, () -> mathEclipseFacade.evaluate("Integrate[log(sin(x^2 + cos(x))) / (1 + x^6), x]")); }

    /** 10706 ms, ~10 seg */
    @DisplayName("evaluate: Integrate[(x^5 * exp(x^2)) / (1 + x^4), x]")
    @Test void benchmarkIntegralExponentialRational() { benchmark(0.001, () -> mathEclipseFacade.evaluate("Integrate[(x^5 * exp(x^2)) / (1 + x^4), x]")); }

    /** 16978 ms, ~17 seg */
    @DisplayName("evaluate: Integrate[(x^4 + 3)/(1 + arctan(x)^2), x]")
    @Test void benchmarkIntegralRationalInverseTan() { benchmark(0.001, () -> mathEclipseFacade.evaluate("Integrate[(x^4 + 3)/(1 + arctan(x)^2), x]")); }

    /** 2449 ms, ~2,5 seg */
    @DisplayName("evaluate: Integrate[Zeta(x^2 + 1), x]")
    @Test void benchmarkIntegralZetaFunction() { benchmark(0.001, () -> mathEclipseFacade.evaluate("Integrate[Zeta(x^2 + 1), x]")); }

    /** 5926 ms, ~6 seg */
    @DisplayName("evaluate: Integrate[(abs(x^3 - x + 1)) / (1 + x^6)^(1/3), x]")
    @Test void benchmarkIntegralAbsRootRational() { benchmark(0.001, () -> mathEclipseFacade.evaluate("Integrate[(abs(x^3 - x + 1)) / (1 + x^6)^(1/3), x]")); }

    /** 36499 ms, ~36 seg */
    @DisplayName("evaluate: Integrate[exp(x) * sin(x^3) / (1 + x^2), x]")
    @Test void benchmarkIntegralOscillatoryComposite() { benchmark(0.001, () -> mathEclipseFacade.evaluate("Integrate[exp(x) * sin(x^3) / (1 + x^2), x]")); }

    /** 4117 ms, ~4 seg */
    @DisplayName("evaluate: Integrate[(x^2 * sqrt(1 - x^2)) / (1 + x^4), x]")
    @Test void benchmarkIntegralEllipticLike() { benchmark(0.001, () -> mathEclipseFacade.evaluate("Integrate[(x^2 * sqrt(1 - x^2)) / (1 + x^4), x]")); }

    private <T> void benchmark(Supplier<T> task) {
        benchmarkInternal(BASE_ITERATIONS, task);
    }

    private <T> void benchmark(double iterationsMultiplier, Supplier<T> task) {
        long newIterations = (long) ((double) BASE_ITERATIONS * iterationsMultiplier);
        if (newIterations <= 0) newIterations = BASE_ITERATIONS;
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
