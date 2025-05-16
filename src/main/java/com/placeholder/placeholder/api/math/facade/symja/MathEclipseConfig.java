package com.placeholder.placeholder.api.math.facade.symja;

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
public class MathEclipseConfig {

    /**
     * Creates and configures a {@link MathEclipseFacade} instance using the core components.
     * This method builds the facade by using utility methods to initialize necessary dependencies.
     *
     * @return an instance of {@code MathEclipseFacade} with required components
     */
    public static MathEclipseFacade buildMathEclipseFacade() {
        return new MathEclipseFacade(
                buildEvalUtilities(),
                new MathEclipseExpressionValidator(),
                buildTeXFormFactory()
        );
    }

    /**
     * Recreates and resets the {@link MathEclipseFacade} with a new {@link EvalUtilities} instance
     * while preserving the provided expression validator and LaTeX factory.
     * <p>
     * This method is useful for resetting the symbolic evaluation context without losing
     * previously configured validation and formatting settings.
     * <p>
     * Parameters:
     * <ul>
     *     <li>{@code expressionValidator} : the validator responsible for ensuring the syntax
     *     of mathematical expressions is correct</li>
     *     <li>{@code laTeXFactory} : the factory responsible for converting symbolic expressions
     *     to LaTeX format</li>
     * </ul>
     *
     * @param expressionValidator the validator for expression syntax
     * @param laTeXFactory        the factory for LaTeX conversion
     * @return a new {@code MathEclipseFacade} instance with a fresh {@code EvalUtilities} object
     */
    public static MathEclipseFacade resetMathEclipseFacade(
            MathEclipseExpressionValidator expressionValidator,
            TeXFormFactory laTeXFactory
    ) {
        return new MathEclipseFacade(
                buildEvalUtilities(),
                expressionValidator,
                laTeXFactory
        );
    }

    /**
     * Creates the core {@link EvalEngine} used by Symja for symbolic evaluation.
     * <p>
     * Parameters:
     * <ul>
     *      <li>{@code sessionID = "default"} : Name of the evaluation context (used for thread isolation or debugging)</li>
     *      <li>{@code recursionLimit = 100}  : Maximum recursion depth (prevents infinite evaluations or stack overflow)</li>
     *      <li>{@code out = null}            : Output stream (null disables output)</li>
     *      <li>{@code relaxedSyntax = true}  : Allows more permissive parsing of expressions (e.g., "()" or "[]" can be used)</li>
     * </ul>
     *
     * @return a configured instance of {@code EvalEngine}
     */
    @Bean
    public EvalEngine evalEngine() {
        return buildEvalEngine();
    }

    /**
     * Builds a default {@link EvalEngine} with a default session ID.
     *
     * @return a new {@code EvalEngine} instance
     */
    public static EvalEngine buildEvalEngine() {
        return buildEvalEngine("default");
    }

    /**
     * Builds a {@link EvalEngine} with a specified session ID.
     *
     * @param sessionID the unique ID for the evaluation session
     * @return a new {@code EvalEngine} instance
     */
    public static EvalEngine buildEvalEngine(String sessionID) {
        return buildEvalEngine(sessionID, 100, 100);
    }

    /**
     * Builds a fully customized {@link EvalEngine} with specified parameters.
     *
     * @param sessionID      the unique ID for the evaluation session
     * @param recursionLimit the maximum allowed recursion depth
     * @param iterationLimit the maximum allowed iteration depth
     * @return a new {@code EvalEngine} instance
     */
    public static EvalEngine buildEvalEngine(String sessionID, int recursionLimit, int iterationLimit) {
        return new EvalEngine(
                sessionID,
                recursionLimit,
                iterationLimit,
                null,
                null,
                true
        );
    }

    /**
     * Creates an {@link EvalUtilities} instance using the provided {@link EvalEngine}.
     * <p>
     * Parameters:
     * <ul>
     *     <li>{@code evalEngine = engine}     : the EvalEngine instance created above</li>
     *     <li>{@code mathNLTagPrefix = false} : disables tagging with MathNL prefixes (used in natural language support)</li>
     *     <li>{@code mathMLHeader = false}    : disables the inclusion of MathML headers in output (keeps it clean)</li>
     * </ul>
     *
     * @param engine the engine to associate with this utility instance
     * @return a configured {@code EvalUtilities} object
     */
    @Bean
    public EvalUtilities evalUtilities(EvalEngine engine) {
        return buildEvalUtilities(engine);
    }

    /**
     * Builds a default {@link EvalUtilities} instance.
     *
     * @return a new {@code EvalUtilities} instance
     */
    public static EvalUtilities buildEvalUtilities() {
        return new EvalUtilities(buildEvalEngine(), false, false);
    }

    /**
     * Builds an {@link EvalUtilities} instance with a specific {@link EvalEngine}.
     *
     * @param engine the evaluation engine to use
     * @return a configured {@code EvalUtilities} instance
     */
    public static EvalUtilities buildEvalUtilities(EvalEngine engine) {
        return new EvalUtilities(engine, false, false);
    }

    /**
     * Builds an {@link EvalUtilities} instance with a specific session ID.
     *
     * @param sessionID the session ID for evaluation context
     * @return a configured {@code EvalUtilities} instance
     */
    public static EvalUtilities buildEvalUtilities(String sessionID) {
        return new EvalUtilities(buildEvalEngine(sessionID), false, false);
    }

    /**
     * Builds an {@link EvalUtilities} instance with specified session ID and limits.
     *
     * @param sessionID      the session ID for evaluation context
     * @param recursionLimit the recursion depth limit
     * @param iterationLimit the iteration depth limit
     * @return a configured {@code EvalUtilities} instance
     */
    public static EvalUtilities buildEvalUtilities(String sessionID, int recursionLimit, int iterationLimit) {
        return new EvalUtilities(buildEvalEngine(sessionID, recursionLimit, iterationLimit), false, false);
    }

    /**
     * Creates the LaTeX form converter used to render Symja expressions to LaTeX.
     * <p>
     * The {@code TeXFormFactory} provides methods to convert symbolic expressions to LaTeX.
     * It has no internal state and can be reused safely.
     *
     * @return a new {@code TeXFormFactory} instance
     */
    @Bean
    public TeXFormFactory teXFormFactory() {
        return buildTeXFormFactory();
    }

    /**
     * Builds a default {@link TeXFormFactory} instance.
     *
     * @return a new {@code TeXFormFactory} instance
     */
    public static TeXFormFactory buildTeXFormFactory() {
        return new TeXFormFactory();
    }
}
