package com.placeholder.placeholder.api.controller;

import com.placeholder.placeholder.api.dto.ExpressionRequest;
import com.placeholder.placeholder.api.services.MathExpressionService;
import com.placeholder.placeholder.util.enums.AppCode;
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
    
    public MathExpressionController(MathExpressionService service) {
        this.service = service;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<ApiResponse<?>> getEvaluatedExpression(@RequestBody ExpressionRequest expressionRequest, HttpServletRequest httpServletRequest) {
        String result = service.evaluate(expressionRequest.expression());

        return new ResponseEntity<>(AppCode.OK.getStatus());
    }
}
