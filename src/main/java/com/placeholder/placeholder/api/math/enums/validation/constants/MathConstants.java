package com.placeholder.placeholder.api.math.enums.validation.constants;

public enum MathConstants {

    PI("Pi"),
    E("E"),
    I("I"),
    PHI("Phi", "GoldenRatio"),
    INFINITY("Infinity"),
    COMPLEX_INFINITY("ComplexInfinity"),
    EULER_GAMMA("EulerGamma"),
    DEGREE("Degree"),
    CATALAN("Catalan"),
    MEISSEL_MERTNESS("MeisselMertens");

    private final String valor1;
    private final String valor2;

    MathConstants(String valor) {
        this.valor1 = valor;
        this.valor2 = null;
    }

    MathConstants(String customValor, String nativeValor) {
        this.valor1 = customValor;
        this.valor2 = nativeValor;
    }

    public String getValor() {
        return valor1;
    }

    public String getCustomValor() {
        return valor1;
    }

    public String getNativeValor() {
        return valor2;
    }
}
