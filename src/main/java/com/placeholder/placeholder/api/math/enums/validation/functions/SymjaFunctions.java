package com.placeholder.placeholder.api.math.enums.validation.functions;

public enum SymjaFunctions implements Functions {

    D("d"),
    DIFF("diff"),
    INTEGRATE("integrate"),
    TAYLOR("taylor"),
    SOLVE("solve"),
    LIMIT("limit"),
    D_SOLVE("dsolve"),
    SIMPLIFY("simplify"),
    EXPAND("expand"),
    DOT("dot"),
    CROSS("cross"),
    NORM("norm"),
    NORMALIZE("normalize"),
    VECTOR_ANGLE("vectorangle"),
    PROJECTION("projection"),
    EIGENVALUES("eigenvalues"),
    INVERSE("inverse"),
    TRANSPOSE("transpose"),
    GCD("gcd"),
    LCM("lcm");

    private final String name;

    SymjaFunctions(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
