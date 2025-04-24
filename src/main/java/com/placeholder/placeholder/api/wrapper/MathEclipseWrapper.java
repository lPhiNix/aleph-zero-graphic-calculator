package com.placeholder.placeholder.api.wrapper;

import org.matheclipse.core.eval.ExprEvaluator;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MathEclipseWrapper {

    private static final int DEFAULT_DECIMAL_SIZE = 10;

    private final ExprEvaluator mathEclipse;

    public MathEclipseWrapper() {
        mathEclipse = new ExprEvaluator();
    }

    public String calculateNumericValue(String expression, int decimals) {
        String formattedExpression = N(expression, decimals);
        return mathEclipse.eval(formattedExpression).toString();
    }

    public String calculateNumericValue(String expression) {
        String formattedExpression = N(expression, DEFAULT_DECIMAL_SIZE);
        return mathEclipse.eval(formattedExpression).toString();
    }

    private String N(String expression, int decimals) {
        return "N[" + expression + ", " + decimals + "]";
    }

    public String calculateDerivate(String expression, String... variables) {
        String formattedExpression = D(expression, variables);
        return mathEclipse.eval(formattedExpression).toString();
    }

    public String calculateDerivate(String expression, Map<String, Integer> variables) {
        String formattedExpression = D(expression, variables);
        return mathEclipse.eval(formattedExpression).toString();
    }

    private String D(String expression, String... variables) {
        String variableList = String.join(", ", variables);
        return "D[" + expression + ", " + variableList + "]";
    }

    private String D(String expression, Map<String, Integer> variables) {
        String derivationPart = variables.entrySet().stream()
                .map(entry -> "{" + entry.getKey() + ", " + entry.getValue() + "}")
                .collect(Collectors.joining(", "));

        return "D[" + expression + ", " + derivationPart + "]";
    }

    public String calculateIndefiniteIntegral(String expression, String... variables) {
        String formattedExpression = I(expression, variables);
        return mathEclipse.eval(formattedExpression).toString();
    }

    private String I(String expression, String... variables) {
        String variableList = String.join(", ", variables);
        return "Integrate[" + expression + ", " + variableList + "]";
    }

    public String calculateIndefiniteIntegral(String expression, Map<String, Integer> variables) {
        String formattedExpression = I(expression, variables);
        return mathEclipse.eval(formattedExpression).toString();
    }

    private String I(String expression, Map<String, Integer> variables) {
        String variablesWithOrder = variables.entrySet().stream()
                .map(entry -> "{" + entry.getKey() + ", " + entry.getValue() + "}")
                .collect(Collectors.joining(", "));

        return "Integrate[" + expression + ", " + variablesWithOrder + "]";
    }

    public static void main(String[] args) {
        try {
            ExprEvaluator util = new ExprEvaluator();

            System.out.println("\n3. Integral:");
            System.out.println(util.eval("D[Sin[x^2], x]")); // Sin[x] - x Cos[x]

            System.out.println("\n4. Simplificación:");
            System.out.println(util.eval("Simplify[(x^2 - 1)/(x - 1)]")); // x + 1

            System.out.println("\n5. Resolver ecuación:");
            System.out.println(util.eval("Solve[x^2 - 4 == 0, x]")); // {{x -> -2}, {x -> 2}}

            System.out.println("\n6. Evaluación numérica (Pi):");
            System.out.println(util.eval("N[sin(1), 50]")); // 50 decimales

            System.out.println("\n7. Inversa de una matriz:");
            System.out.println(util.eval("Inverse[{{1, 2}, {3, 4}}]")); // {{-2, 1}, {3/2, -1/2}}

            System.out.println("\n8. Límite:");
            System.out.println(util.eval("Limit[Sin[x]/x, x -> 0]")); // 1

            System.out.println("\n9. Serie de Taylor:");
            System.out.println(util.eval("Series[Exp[x], {x, 0, 4}]")); // 1 + x + x²/2 + ...

            System.out.println("\n10. 'Plot' simbólico:");
            System.out.println(util.eval("Table[{x, Sin[x]}, {x, 0, Pi, Pi/4}]")); // Datos para graficar

            System.out.println("\n11. Lógica booleana:");
            System.out.println(util.eval("And[True, Or[False, True]]")); // True

            System.out.println("\n12. Números complejos:");
            System.out.println(util.eval("Abs[3 + 4*I]")); // 5

            System.out.println("\n13. Función definida por el usuario:");
            util.eval("f[x_] := x^2 + 2*x + 1");
            System.out.println(util.eval("f[3]")); // 16

        } catch (Exception e) {
            System.out.println("Error al evaluar expresión: " + e.getMessage());
        }
    }
}
