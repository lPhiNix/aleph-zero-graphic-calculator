package com.placeholder.placeholder.api.math.enums.validation.functions;

import com.placeholder.placeholder.api.math.enums.validation.io.MathInputType;
import com.placeholder.placeholder.api.math.enums.validation.io.MathOutputType;

public enum SymjaFunctions implements Functions {

    D("d", MathOutputType.FUNCTION, MathInputType.NUMBER_OR_FUNCTION),
    DIFF("diff", MathOutputType.FUNCTION, MathInputType.NUMBER_OR_FUNCTION),
    INTEGRATE("integrate", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION, MathInputType.SYMBOL_OR_VECTOR),
    TAYLOR("taylor", MathOutputType.FUNCTION, MathInputType.FUNCTION),
    SOLVE("solve", MathOutputType.VECTOR, MathInputType.EQUATION),
    LIMIT("limit", MathOutputType.NUMERIC, MathInputType.LIMIT_NOTATION),
    D_SOLVE("dsolve", MathOutputType.VECTOR, MathInputType.EQUATION),

    SIMPLIFY("simplify", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),
    EXPAND("expand", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),

    DOT("dot", MathOutputType.NUMERIC, MathInputType.VECTOR, MathInputType.VECTOR),
    CROSS("cross", MathOutputType.VECTOR, MathInputType.VECTOR, MathInputType.VECTOR),
    NORM("norm", MathOutputType.NUMERIC, MathInputType.VECTOR),
    NORMALIZE("normalize", MathOutputType.VECTOR, MathInputType.VECTOR),
    VECTOR_ANGLE("vectorangle", MathOutputType.NUMERIC, MathInputType.VECTOR, MathInputType.VECTOR),
    PROJECTION("projection", MathOutputType.VECTOR, MathInputType.VECTOR, MathInputType.VECTOR),

    EIGENVALUES("eigenvalues", MathOutputType.VECTOR, MathInputType.VECTOR, MathInputType.VECTOR),
    INVERSE("inverse", MathOutputType.MATRIX, MathInputType.MATRIX),
    TRANSPOSE("transpose", MathOutputType.MATRIX, MathInputType.MATRIX),

    GCD("gcd", MathOutputType.NUMERIC, MathInputType.NUMBER, MathInputType.NUMBER),
    LCM("lcm", MathOutputType.NUMERIC, MathInputType.NUMBER, MathInputType.NUMBER);

    private final String name;
    private final MathInputType[] inputType;
    private final MathOutputType resultType;

    SymjaFunctions(String name, MathOutputType resultType, MathInputType
            ... inputType) {
        this.name = name;
        this.inputType = inputType;
        this.resultType = resultType;
    }

    @Override
    public String getName() {
        return name;
    }

    public MathOutputType getResultType() {
        return resultType;
    }
}
