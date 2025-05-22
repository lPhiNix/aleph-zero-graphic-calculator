package com.placeholder.placeholder.api.math.enums.validation.functions;

import com.placeholder.placeholder.api.math.enums.validation.io.MathOutputType;

public interface Functions {
    String getName();
    MathOutputType getResultType();

    static Functions fromName(String name, Functions[] functions) {
        for (Functions f : functions) {
            if (f.getName().equalsIgnoreCase(name)) {
                return f;
            }
        }
        return null;
    }
}
