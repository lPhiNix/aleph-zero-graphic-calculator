package com.alephzero.alephzero.api.math.service.classifier;

import com.alephzero.alephzero.api.math.enums.computation.MathExpressionType;

/**
 * Interface {@code Classifier} defines the contract for classes that classify
 * mathematical expressions into predefined types.
 * <p>
 * Implementations of this interface analyze a given expression and determine
 * its classification based on syntactic or semantic characteristics.
 *
 * @see MathExpressionType
 */
public interface Classifier {
    /**
     * Classifies a given mathematical expression string into a specific type.
     *
     * @param expression the mathematical expression to classify; can be any valid or invalid expression string
     * @return the {@link MathExpressionType} corresponding to the classification result,
     *         or {@code UNKNOWN} if it cannot be classified
     */
    MathExpressionType classify(String expression);
}
