package com.placeholder.placeholder.api.facade.controller;

import com.placeholder.placeholder.api.services.MathExpressionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/math_expression")
public class MathExpressionController {
    private final MathExpressionService service;
    
    public MathExpressionController(MathExpressionService service) {
        this.service = service;
    }
    
    @GetMapping("/evaluate")
    public String getEvaluatedResult(@RequestBody String expression) {
        return service.evaluate(expression);
    }

    @GetMapping("/calculate")
    public String getCalculatedResult(@RequestBody String expression, int decimals) {
        return service.calculate(expression, decimals);
    }
}
