package com.alephzero.alephzero.api.math.enums.validation.functions;

/**
 * {@code MathFunctions} enumerates the mathematical functions that users can input
 * and that are recognized as valid by the system.
 * <p>
 * These functions are typically mathematical or trigonometric and may be used
 * in symbolic or numeric expressions. The enum values map to lowercase string
 * identifiers as expected by the expression evaluator.
 */
public enum MathFunctions implements Functions {

    // User-defined functions
    F("f"),
    G("g"),
    H("h"),

    // Parametric variables often treated as functions
    X("x"),
    Y("Y"),
    Z("z"),

    // Special functions
    GAMMA("gamma"),
    ZETA("zeta"),
    ERF("erf"),
    FRESNEL_C("fresnelc"),
    C("c"),

    // Basic mathematical operations
    SQRT("sqrt"),
    EXP("exp"),
    LOG("log"),
    LOG_10("log10"),
    LOG_2("log2"),
    ABS("abs"),

    // Trigonometric functions
    SIN("sin"),
    COS("cos"),
    TAN("tan"),
    CSC("csc"),
    COT("cot"),
    SEC("sec"),

    // Inverse trigonometric functions
    ARC_SIN("arcsin"),
    ARC_COS("arccos"),
    ARC_TAN("arctan"),
    ARC_CSC("arccsc"),
    ARC_COT("arccot"),
    ARC_SEC("arcsec"),

    // Hyperbolic functions
    SIN_H("sinh"),
    COS_H("cosh"),
    TAN_H("tanh"),
    COT_H("coth"),
    SEC_H("sech"),
    CSC_H("csch"),

    // Inverse hyperbolic functions
    ARC_SIN_H("arcsinh"),
    ARC_COS_H("arccosh"),
    ARC_TAN_H("arctanh"),
    ARC_COT_H("arccoth"),
    ARC_SEC_H("arcsech"),
    ARC_CSC_H("arccsch");

    /** String name of the function, as it should appear in expressions. */
    private final String name;

    /**
     * Constructs a new MathFunction with the given name.
     *
     * @param name the string representation of the function
     */
    MathFunctions(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }
}
