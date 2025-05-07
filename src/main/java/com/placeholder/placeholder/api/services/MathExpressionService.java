package com.placeholder.placeholder.api.services;

import com.placeholder.placeholder.api.facade.MathEclipseFacade;
import org.springframework.stereotype.Service;

@Service
public class MathExpressionService {
    private final MathEclipseFacade mathEclipse;

    public MathExpressionService(MathEclipseFacade mathEclipse) {
        this.mathEclipse = mathEclipse;
    }
}
