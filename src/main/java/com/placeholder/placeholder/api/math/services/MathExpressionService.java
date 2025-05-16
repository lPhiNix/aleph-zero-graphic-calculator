package com.placeholder.placeholder.api.math.services;

import com.placeholder.placeholder.api.math.dto.request.MathExpressionRequest;
import com.placeholder.placeholder.api.math.dto.request.MathExpression;
import com.placeholder.placeholder.api.math.dto.response.MathExpressionEvaluation;
import com.placeholder.placeholder.api.math.dto.response.ExpressionResultResponse;
import com.placeholder.placeholder.api.math.dto.response.MathEvaluation;
import com.placeholder.placeholder.api.math.facade.MathLibFacade;
import com.placeholder.placeholder.api.math.facade.symja.MathEclipseEvaluation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MathExpressionService {

    private final MathLibFacade mathEclipse;

    public MathExpressionService(MathLibFacade mathEclipse) {
        this.mathEclipse = mathEclipse;
    }



    /**
     * A functional interface for math operations on expressions.
     */
    @FunctionalInterface
    public interface MathOperation {
        MathEvaluation compute(MathExpression expression);
    }
}
