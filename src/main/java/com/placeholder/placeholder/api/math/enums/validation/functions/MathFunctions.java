package com.placeholder.placeholder.api.math.enums.validation.functions;

public enum MathFunctions implements Functions {

    F("f"),
    G("g"),
    H("h"),

    X("x"),
    Y("Y"),
    Z("z"),

    GAMMA("gamma"),
    ZETA("zeta"),
    ERF("erf"),
    FRESNEL_C("fresnelc"),
    C("c"),

    SQRT("sqrt"),
    EXP("exp"),
    LOG("log"),
    LOG_10("log10"),
    LOG_2("log2"),
    ABS("abs"),

    SIN("sin"),
    COS("cos"),
    TAN("tan"),
    CSC("csc"),
    COT("cot"),
    SEC("sec"),

    ARC_SIN("arcsin"),
    ARC_COS("arccos"),
    ARC_TAN("arctan"),
    ARC_CSC("arccsc"),
    ARC_COT("arccot"),
    ARC_SEC("arcsec"),

    SIN_H("sinh"),
    COS_H("cosh"),
    TAN_H("tanh"),
    COT_H("coth"),
    SEC_H("sech"),
    CSC_H("csch"),

    ARC_SIN_H("arcsinh"),
    ARC_COS_H("arccosh"),
    ARC_TAN_H("arctanh"),
    ARC_COT_H("arccoth"),
    ARC_SEC_H("arcsech"),
    ARC_CSC_H("arccsch");

    private final String name;

    MathFunctions(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
