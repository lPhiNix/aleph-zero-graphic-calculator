package com.placeholder.placeholder.api.math.service.classifier;

import com.placeholder.placeholder.api.math.enums.computation.MathExpressionType;

public interface Classifier {
    MathExpressionType classify(String expression);
}
