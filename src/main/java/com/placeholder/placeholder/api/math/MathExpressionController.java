package com.placeholder.placeholder.api.math;

import com.placeholder.placeholder.api.math.dto.ExpressionRequest;
import com.placeholder.placeholder.api.math.dto.ExpressionResultResponse;
import com.placeholder.placeholder.api.math.services.MathExpressionService;
import com.placeholder.placeholder.api.util.common.messages.ApiResponseFactory;
import com.placeholder.placeholder.api.util.common.messages.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/math_expression")
public class MathExpressionController {

    private final MathExpressionService service;
    private final ApiResponseFactory apiResponseFactory;
    
    public MathExpressionController(MathExpressionService service, ApiResponseFactory apiResponseFactory) {
        this.service = service;
        this.apiResponseFactory = apiResponseFactory;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<ApiResponse<ExpressionResultResponse>> evaluate(@RequestBody ExpressionRequest expressionRequest) {
        String result = service.evaluate(expressionRequest.expression());
        ExpressionResultResponse response = new ExpressionResultResponse(result);

        return apiResponseFactory.ok(response);
    }
}
