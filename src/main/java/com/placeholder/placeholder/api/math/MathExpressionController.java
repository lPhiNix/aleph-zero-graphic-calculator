package com.placeholder.placeholder.api.math;

import com.placeholder.placeholder.api.math.dto.request.MathEvaluationRequest;
import com.placeholder.placeholder.api.math.dto.response.MathEvaluationResultResponse;
import com.placeholder.placeholder.api.math.service.core.MathExpressionService;
import com.placeholder.placeholder.api.util.common.messages.ApiMessageFactory;
import com.placeholder.placeholder.api.util.common.messages.ApiResponseFactory;
import com.placeholder.placeholder.api.util.common.messages.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that exposes endpoints related to mathematical expression processing.
 * <p>
 * Handles operations such as evaluation of mathematical expressions using a service layer.
 * </p>
 */
@RestController
@RequestMapping("api/v1/math")
@RequiredArgsConstructor
public class MathExpressionController {

    private final MathExpressionService service;
    private final ApiMessageFactory messageFactory;


    /**
     * Evaluates one or more mathematical expressions with optional formatting settings.
     *
     * @param mathExpressionRequest A request object containing a list of expressions to evaluate and optional evaluation data
     * @return A {@link ResponseEntity} containing an {@link ApiResponse} with the evaluation result
     */
    @PostMapping("/evaluation")
    public ResponseEntity<ApiResponse<MathEvaluationResultResponse>> evaluation(
            @RequestBody @Valid MathEvaluationRequest mathExpressionRequest
    ) {
        MathEvaluationResultResponse response = service.evaluation(mathExpressionRequest);
        return messageFactory.response(response).ok().build();
    }
}
