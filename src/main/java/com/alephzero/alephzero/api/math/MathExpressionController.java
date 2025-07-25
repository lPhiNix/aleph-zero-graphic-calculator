package com.alephzero.alephzero.api.math;

import com.alephzero.alephzero.api.math.dto.request.MathEvaluationRequest;
import com.alephzero.alephzero.api.math.dto.response.MathEvaluationResultResponse;
import com.alephzero.alephzero.api.math.service.core.MathExpressionService;
import com.alephzero.alephzero.api.util.common.messages.ApiMessageFactory;
import com.alephzero.alephzero.api.util.common.messages.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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