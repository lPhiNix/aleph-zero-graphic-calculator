package com.alephzero.alephzero.api.math.enums.validation.constants;

/**
 * {@code MathConstants} is an enumeration of predefined mathematical constants
 * recognized by the application. These constants may appear in user input expressions
 * and are validated and interpreted accordingly.
 * <p>
 * Each constant can have one or two string representations:
 * <ul>
 *     <li>{@code valor1} — A custom or default string identifier used in the frontend or by the user.</li>
 *     <li>{@code valor2} — An optional native representation typically used by libraries like Symja.</li>
 * </ul>
 * For example, the constant {@code PHI} may be input as "Phi" (valor1) but is internally mapped to "GoldenRatio" (valor2).
 */
public enum MathConstants {

    /** The mathematical constant π (pi). */
    PI("Pi"),

    /** The Euler number e. */
    E("E"),

    /** The imaginary unit i. */
    I("I"),

    /** The golden ratio φ, also known as GoldenRatio in Symja. */
    PHI("Phi", "GoldenRatio"),

    /** Represents the concept of infinity. */
    INFINITY("Infinity"),

    /** Represents complex infinity. */
    COMPLEX_INFINITY("ComplexInfinity"),

    /** Euler–Mascheroni constant γ. */
    EULER_GAMMA("EulerGamma"),

    /** Degree unit used in angle measurements. */
    DEGREE("Degree"),

    /** Catalan's constant. */
    CATALAN("Catalan"),

    /** Meissel–Mertens constant. */
    MEISSEL_MERTNESS("MeisselMertens"),

    /** Glaisher–Kinkelin constant. */
    GLAISHER("Glaisher"),

    /** Khinchin's constant. */
    KHINCHIN("Khinchin");

    /** The custom or default string identifier used by the user or frontend. */
    private final String valor1;

    /** The native or alternative identifier (e.g. for Symja), if applicable. */
    private final String valor2;

    /**
     * Constructor for constants with only one identifier.
     *
     * @param valor the string identifier of the constant
     */
    MathConstants(String valor) {
        this.valor1 = valor;
        this.valor2 = null;
    }

    /**
     * Constructor for constants with both a custom and native identifier.
     *
     * @param customValor the custom or frontend identifier
     * @param nativeValor the native library-specific identifier (e.g. Symja)
     */
    MathConstants(String customValor, String nativeValor) {
        this.valor1 = customValor;
        this.valor2 = nativeValor;
    }

    /**
     * Gets the main string identifier of the constant.
     *
     * @return the custom or default value
     */
    public String getValor() {
        return valor1;
    }

    /**
     * Gets the custom representation of the constant.
     *
     * @return the custom value
     */
    public String getCustomValor() {
        return valor1;
    }

    /**
     * Gets the native or alternative representation of the constant, if any.
     *
     * @return the native value, or {@code null} if not defined
     */
    public String getNativeValor() {
        return valor2;
    }
}
