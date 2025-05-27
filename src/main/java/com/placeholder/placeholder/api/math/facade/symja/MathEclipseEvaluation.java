package com.placeholder.placeholder.api.math.facade.symja;

import com.placeholder.placeholder.api.math.facade.MathExpressionEvaluation;

import java.util.*;

/**
 * {@code MathEclipseEvaluation} represents the result of a mathematical expression evaluation in the
 * {@code Symja MathEclipse} mathematical expression evaluation and processing library.
 * This class stores the evaluated expression and any errors encountered during the process.
 */
public class MathEclipseEvaluation implements MathExpressionEvaluation {
    private final String expressionEvaluated; // The evaluated expression
    private final Set<String> evaluationProblems; // Set of errors encountered during evaluation

    public MathEclipseEvaluation(String expressionEvaluated) {
        this.expressionEvaluated = expressionEvaluated;
        this.evaluationProblems = new LinkedHashSet<>();
    }

    /**
     * Returns the evaluated mathematical expression.
     *
     * @return the evaluated expression
     */
    @Override
    public String getExpressionEvaluated() {
        return expressionEvaluated;
    }

    /**
     * Returns the list of all evaluation problems, if any, as an Optional.
     *
     * @return an Optional containing the list of errors, or an empty Optional if no errors occurred
     */
    @Override
    public Optional<List<String>> getEvaluationProblems() {
        return evaluationProblems.isEmpty()
                ? Optional.empty()
                : Optional.of(new ArrayList<>(evaluationProblems));
    }

    /**
     * Adds a new error message to the set of evaluation problems.
     *
     * @param errorMessage the error message to add
     * @return true if the error was added, false if it was null or already present
     */
    public boolean addError(String errorMessage) {
        if (errorMessage == null) {
            return false;
        }
        return evaluationProblems.add(errorMessage);
    }

    /**
     * Adds multiple error messages from a newline-separated string.
     *
     * @param errors the string containing multiple error messages
     * @return true if at least one error was added, false otherwise
     */
    public boolean addErrorsFromErrorStream(String errors) {
        String[] errorList = errors.split("\\n");
        boolean added = false;

        for (String error : errorList) {
            error = error.trim();
            if (!error.isEmpty() && addError(error)) {
                added = true;
            }
        }

        return added;
    }

    /**
     * Retrieves the last error in the set of evaluation problems.
     *
     * @return the most recent error message, or null if no errors are present
     */
    public String getLastError() {
        return evaluationProblems.isEmpty() ? null : evaluationProblems.toArray(new String[0])[evaluationProblems.size() - 1];
    }

    /**
     * Returns the number of unique errors encountered during evaluation.
     *
     * @return the number of errors
     */
    public int getErrorAmount() {
        return evaluationProblems.size();
    }

    /**
     * Clears all stored evaluation errors.
     */
    public void clearErrors() {
        evaluationProblems.clear();
    }
}
