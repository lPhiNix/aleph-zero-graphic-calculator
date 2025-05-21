package com.placeholder.placeholder.api.math.service.micro;

import com.placeholder.placeholder.api.math.enums.MathExpressionType;
import com.placeholder.placeholder.api.math.facade.MathLibFacade;
import com.placeholder.placeholder.api.math.service.memory.MathAssignmentMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class MathExpressionClassifier {

    private final MathLibFacade mathEclipse;
    private final MathAssignmentMemory assignmentMemory;

    @Autowired
    public MathExpressionClassifier(MathLibFacade mathEclipse, MathAssignmentMemory assignmentMemory) {
        this.mathEclipse = mathEclipse;
        this.assignmentMemory = assignmentMemory;
    }

    public static final Pattern FUNCTION_PATTERN = Pattern.compile("(?=.*[a-zA-Z])[-+*/^()a-zA-Z0-9\\s]+");
    public static final Pattern ASSIGNMENT_PATTERN = Pattern.compile("\\s*[a-z]\\s*=\\s*.+");
    public static final Pattern EQUATION_PATTERN = Pattern.compile(".+==.+");
    public static final Pattern NUMERIC_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?([+-]\\d+i)?|-\\d+i|\\d+/\\d+");
    public static final Pattern MATRIX_PATTERN = Pattern.compile("\\{\\{\\s*-?\\d+(\\.\\d+)?(\\s*,\\s*-?\\d+(\\.\\d+)?)*\\s*}(,\\s*\\{\\s*-?\\d+(\\.\\d+)?(\\s*,\\s*-?\\d+(\\.\\d+)?)*\\s*})+}");
    public static final Pattern VECTOR_PATTERN = Pattern.compile("\\{\\s*-?\\d+(\\.\\d+)?(\\s*,\\s*-?\\d+(\\.\\d+)?)*\\s*}");

    public MathExpressionType detectType(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            return MathExpressionType.NONE;
        }

        String trimmedExpr = expression.trim();
        if (EQUATION_PATTERN.matcher(trimmedExpr).matches()) {
            return MathExpressionType.EQUATION;
        } else if (ASSIGNMENT_PATTERN.matcher(trimmedExpr).matches()) {
            return MathExpressionType.ASSIGNMENT;
        } else if (MATRIX_PATTERN.matcher(trimmedExpr).matches()) {
            return MathExpressionType.MATRIX;
        } else if (VECTOR_PATTERN.matcher(trimmedExpr).matches()) {
            return MathExpressionType.VECTOR;
        } else if (NUMERIC_PATTERN.matcher(trimmedExpr).matches()) {
            return MathExpressionType.NUMERIC;
        } else if (FUNCTION_PATTERN.matcher(trimmedExpr).matches()) {
            return MathExpressionType.FUNCTION;
        }

        return MathExpressionType.UNKNOWN;
    }
}
