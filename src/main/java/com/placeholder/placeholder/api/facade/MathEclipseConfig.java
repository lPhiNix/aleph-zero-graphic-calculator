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
        // - sessionID = "default" : Name of the evaluation context (used for thread isolation or debugging)
        // - recursionLimit = 100  : Maximum recursion depth (prevents infinite evaluations or stack overflow)
        // - out = null            : Output stream for debug/logging (null disables output)
        // - relaxedSyntax = true  : Allows more permissive parsing of expressions (e.g., "()" or "[]" can be used)
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
        // - evalEngine = engine     : the EvalEngine instance created above
        // - mathNLTagPrefix = false : disables tagging with MathNL prefixes (used in natural language support)
        // - mathMLHeader = false    : disables the inclusion of MathML headers in output (keeps it clean)
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
