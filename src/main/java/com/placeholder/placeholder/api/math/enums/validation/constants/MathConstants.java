package com.placeholder.placeholder.api.math.enums.validation.constants;

public enum MathConstants {

    PI("Pi"),
    E("E"),
    I("I"),
    PHI("GoldenRatio"),
    INFINITY("infinity"),
    COMPLEX_INFINITY("complexinfinity"),
    ZETA("zeta"),
    GAMMA("gamma"),
    ERF("erf");

    private final String valor;

    MathConstants(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}
