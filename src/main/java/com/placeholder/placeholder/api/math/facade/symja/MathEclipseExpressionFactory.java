package com.placeholder.placeholder.api.math.facade.symja;

import org.springframework.stereotype.Component;

@Component
public class MathEclipseExpressionFactory {
    /**
     * Wraps an expression with Symja's N[] function for numeric approximation.
     *
     * @param expression input expression
     * @param decimals number of decimals
     * @return wrapped expression
     */
    public String N(String expression, int decimals) {
        return "N[" + expression + ", " + decimals + "]";
    }

    /**
     * Builds a Symja-compatible Plot[] expression.
     *
     * @param expression the function to plot
     * @param variable variable of the function
     * @param origin start of the domain
     * @param bound end of the domain
     * @return constructed plot expression
     */
    public String Plot(String expression, String variable, String origin, String bound) {
        return "Plot[" + expression + ", {" + variable + ", " + origin + ", " + bound + "}]";
    }

    public String removeFreezeFromExpression(String expression) {
        //System.out.println("FREEZE: " + expression); // TODO


        // Convertir a minúsculas para hacerlo insensible al caso
        String lowerExpression = expression.toLowerCase();

        // Comprobar si la expresión empieza con "hold[" o "hold("
        if (lowerExpression.startsWith("hold[") || lowerExpression.startsWith("hold(")) {
            // Eliminar el prefijo "Hold[" o "Hold("
            expression = expression.substring(5); // "Hold[" o "Hold("

            // Comprobar si la expresión termina con "]" o ")"
            if (expression.endsWith("]") || expression.endsWith(")")) {
                // Eliminar el sufijo "]" o ")"
                expression = expression.substring(0, expression.length() - 1); // "]" o ")"
            }
        }

        return expression;
    }
}
