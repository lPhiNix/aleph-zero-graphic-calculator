package com.placeholder.placeholder.api.facade.controller;

import com.placeholder.placeholder.api.services.MathExpressionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/math_expression")
public class MathExpressionController {
    private final MathExpressionService service;
    
    public MathExpressionController(MathExpressionService service) {
        this.service = service;
    }
    
    @GetMapping("/evaluate")
    public String getEvaluatedResult(@RequestParam String expression) {
        
    }
}
