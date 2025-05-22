package com.placeholder.placeholder.api.math.enums.validation.functions;

import com.placeholder.placeholder.api.math.enums.validation.io.MathInputType;
import com.placeholder.placeholder.api.math.enums.validation.io.MathOutputType;
import lombok.Getter;

public enum MathFunctions implements Functions {

    F("f", MathOutputType.FUNCTION, MathInputType.FUNCTION),
    G("g", MathOutputType.FUNCTION, MathInputType.FUNCTION),
    H("h", MathOutputType.FUNCTION, MathInputType.FUNCTION),

    X("x", MathOutputType.FUNCTION, MathInputType.VARIABLE),
    Y("Y", MathOutputType.FUNCTION, MathInputType.VARIABLE),
    Z("z", MathOutputType.FUNCTION, MathInputType.VARIABLE),

    GAMMA("gamma", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),
    ZETA("zeta", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),
    ERF("erf", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),
    FRESNEL_C("fresnelc", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),
    C("c", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),

    SQRT("sqrt", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),
    EXP("exp", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),
    LOG("log", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),
    LOG_10("log10", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),
    LOG_2("log2", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),
    ABS("abs", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),

    SIN("sin", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),
    COS("cos", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),
    TAN("tan", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),
    CSC("csc", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),
    COT("cot", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),
    SEC("sec", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),

    ARC_SIN("arcsin", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),
    ARC_COS("arccos", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),
    ARC_TAN("arctan", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),
    ARC_CSC("arccsc", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),
    ARC_COT("arccot", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),
    ARC_SEC("arcsec", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),

    SIN_H("sinh", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),
    COS_H("cosh", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),
    TAN_H("tanh", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),
    COT_H("coth", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),
    SEC_H("sech", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),
    CSC_H("csch", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),

    ARC_SIN_H("arcsinh", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),
    ARC_COS_H("arccosh", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),
    ARC_TAN_H("arctanh", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),
    ARC_COT_H("arccoth", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),
    ARC_SEC_H("arcsech", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION),
    ARC_CSC_H("arccsch", MathOutputType.NEED_PREVIOUS_EVALUATION, MathInputType.NUMBER_OR_FUNCTION);

    private final String name;

    @Getter private final MathInputType[] inputType;
    @Getter private final MathOutputType resultType;

    MathFunctions(String name, MathOutputType resultType, MathInputType... inputType) {
        this.name = name;
        this.inputType = inputType;
        this.resultType = resultType;
    }

    @Override
    public String getName() {
        return name;
    }

}
