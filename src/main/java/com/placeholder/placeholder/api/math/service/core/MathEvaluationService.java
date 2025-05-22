package com.placeholder.placeholder.api.math.service.core;

import com.placeholder.placeholder.api.math.dto.request.MathEvaluationRequest;
import com.placeholder.placeholder.api.math.dto.response.MathEvaluationResultResponse;

public interface MathEvaluationService {

    MathEvaluationResultResponse evaluation(MathEvaluationRequest request);
}
