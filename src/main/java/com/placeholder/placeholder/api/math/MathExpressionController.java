package com.placeholder.placeholder.api.math;

import com.placeholder.placeholder.api.math.dto.request.MathEvaluationRequest;
import com.placeholder.placeholder.api.math.dto.response.MathEvaluationResultResponse;
import com.placeholder.placeholder.api.math.services.MathExpressionService;
import com.placeholder.placeholder.api.util.common.messages.ApiResponseFactory;
import com.placeholder.placeholder.api.util.common.messages.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/math")
public class MathExpressionController {

    private final MathExpressionService service;
    private final ApiResponseFactory apiResponseFactory;
    
    public MathExpressionController(MathExpressionService service, ApiResponseFactory apiResponseFactory) {
        this.service = service;
        this.apiResponseFactory = apiResponseFactory;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<ApiResponse<MathEvaluationResultResponse>> evaluate(@RequestBody @Valid MathEvaluationRequest mathExpressionRequest) {
        MathEvaluationResultResponse response = service.evaluation(mathExpressionRequest);
        return apiResponseFactory.ok(response);
    }
}
