package com.placeholder.placeholder.api.math.facade;

import java.util.List;
import java.util.Optional;

public interface MathExpressionEvaluation {
    String getExpressionEvaluated();

    default Optional<List<String>> getEvaluationProblems() {
        return Optional.empty();
    }
}
