package com.placeholder.placeholder.api.math.enums.validation.functions;

/**
 * {@code SymjaFunctions} enumerates advanced symbolic computation functions
 * supported by the Symja engine, which can be used in user expressions
 * for symbolic algebra, calculus, and linear algebra.
 */
public enum SymjaFunctions implements Functions {

    // Derivatives and calculus
    D("d"),
    DIFF("diff"),
    INTEGRATE("integrate"),
    TAYLOR("taylor"),
    LIMIT("limit"),
    SOLVE("solve"),
    D_SOLVE("dsolve"),

    // Algebra and simplification
    SIMPLIFY("simplify"),
    EXPAND("expand"),

    // Vector and matrix operations
    DOT("dot"),
    CROSS("cross"),
    NORM("norm"),
    NORMALIZE("normalize"),
    VECTOR_ANGLE("vectorangle"),
    PROJECTION("projection"),
    EIGENVALUES("eigenvalues"),
    INVERSE("inverse"),
    TRANSPOSE("transpose"),

    // Arithmetic functions
    GCD("gcd"),
    LCM("lcm");

    /** String name of the function as recognized by the Symja engine. */
    private final String name;

    /**
     * Constructs a new SymjaFunction with the given name.
     *
     * @param name the string representation of the Symja function
     */
    SymjaFunctions(String name) {
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
