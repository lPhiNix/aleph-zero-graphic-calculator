package com.placeholder.placeholder.api.controller;

import com.placeholder.placeholder.api.dto.ExpressionRequest;
import com.placeholder.placeholder.api.dto.ExpressionResultResponse;
import com.placeholder.placeholder.api.services.MathExpressionService;
import com.placeholder.placeholder.util.messages.ApiResponseFactory;
import com.placeholder.placeholder.util.messages.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<ApiResponse<ExpressionResultResponse>> evaluate(@RequestBody ExpressionRequest expressionRequest, HttpServletRequest httpServletRequest) {
        ExpressionResultResponse result = service.getEvaluation(expressionRequest.expression());

        return apiResponseFactory.ok(httpServletRequest.getRequestURI(), "nose", result);
    }
}
