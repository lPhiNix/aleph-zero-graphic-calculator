package com.placeholder.placeholder.api.facade;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.form.tex.TeXFormFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class MathEclipseEngineConfig {
    @Bean
    public EvalEngine evalEngine() {
        return new EvalEngine("default", 100, null, true);
    }

    @Bean
    public EvalUtilities evalUtilities(EvalEngine engine) {
        return new EvalUtilities(engine, false, false);
    }

    @Bean
    public TeXFormFactory teXFormFactory() {
        return new TeXFormFactory();
    }
}