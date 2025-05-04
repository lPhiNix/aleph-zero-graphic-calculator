package com.placeholder.placeholder.api.facade;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.form.tex.TeXFormFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@code MathEclipseConfig} is a Spring configuration class responsible for
 * creating and wiring the core Symja (MathEclipse) engine components as Spring beans.
 *
 * <p>It exposes the following beans to the application context:
 * <ul>
 *     <li>{@link EvalEngine} - the core symbolic evaluation engine</li>
 *     <li>{@link EvalUtilities} - a utility class that wraps expression parsing and evaluation</li>
 *     <li>{@link TeXFormFactory} - a formatter for converting expressions to LaTeX</li>
 * </ul>
 *
 * <p>All components are designed for reuse across services and facades in the system.</p>
 */
@Configuration
class MathEclipseConfig {

    /**
     * Creates the core {@link EvalEngine} used by Symja for symbolic evaluation.
     *
     * @return a configured instance of {@code EvalEngine}
     */
    @Bean
    public EvalEngine evalEngine() {
        // Parameters:
        // - "default" : Name of the evaluation context (used for thread isolation or debugging)
        // - 100       : Maximum recursion depth (prevents infinite evaluations or stack overflow)
        // - null      : nose TODO
        // - true      : nose TODO
        return new EvalEngine("default", 100, null, true);
    }

    /**
     * Creates an {@link EvalUtilities} instance using the provided {@link EvalEngine}.
     *
     * @param engine the engine to associate with this utility instance
     * @return a configured {@code EvalUtilities} object
     */
    @Bean
    public EvalUtilities evalUtilities(EvalEngine engine) {
        // Parameters:
        // - engine : the EvalEngine instance created above
        // - false  : nose TODO
        // - false  : nose TODO
        //
        // This configuration keeps the output clean and optimized for production usage.
        return new EvalUtilities(engine, false, false);
    }

    /**
     * Creates the LaTeX form converter used to render Symja expressions to LaTeX.
     *
     * @return a new {@code TeXFormFactory} instance
     */
    @Bean
    public TeXFormFactory teXFormFactory() {
        // The TeXFormFactory provides methods to convert symbolic expressions to LaTeX
        // It has no internal state and can be reused safely
        return new TeXFormFactory();
    }
}