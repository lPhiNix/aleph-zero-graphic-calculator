package com.placeholder.placeholder.api.math.facade.symja;

import com.placeholder.placeholder.api.math.facade.MathExpressionEvaluation;

import java.util.*;

public class MathEclipseEvaluation implements MathExpressionEvaluation {
    private final String expressionEvaluated;
    private final Set<String> evaluationProblems;

    public MathEclipseEvaluation(String expressionEvaluated) {
        this.expressionEvaluated = expressionEvaluated;
        this.evaluationProblems = new LinkedHashSet<>();
    }

    @Override
    public String getExpressionEvaluated() {
        return expressionEvaluated;
    }

    @Override
    public Optional<List<String>> getEvaluationProblems() {
        return evaluationProblems.isEmpty()
                ? Optional.empty()
                : Optional.of(new ArrayList<>(evaluationProblems));
    }

    public boolean addError(String errorMessage) {
        if (errorMessage == null) {
            return false;
        }

        return evaluationProblems.add(errorMessage);
    }

    public boolean addErrorsFromErrorStream(String errors) {
        String[] errorList = errors.split("\\n");

        for (String error : errorList) {
            error = error.replace("\n", "");

            if (!addError(error)) return false;
        }

        return true;
    }

    public String getLastError() {
        return evaluationProblems.toArray(new String[0])[getErrorAmount()];
    }

    public int getErrorAmount() {
        return evaluationProblems.size();
    }

    public void clearErrors() {
        evaluationProblems.clear();
    }
}
