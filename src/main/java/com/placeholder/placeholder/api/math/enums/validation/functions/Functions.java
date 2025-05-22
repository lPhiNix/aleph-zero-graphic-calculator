package com.placeholder.placeholder.api.math.enums.validation.functions;

public interface Functions {
    String getName();

    static Functions fromName(String name, Functions[] functions) {
        for (Functions f : functions) {
            if (f.getName().equalsIgnoreCase(name)) {
                return f;
            }
        }
        return null;
    }
}
