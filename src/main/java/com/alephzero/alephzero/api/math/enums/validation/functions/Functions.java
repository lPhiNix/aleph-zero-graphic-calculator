package com.alephzero.alephzero.api.math.enums.validation.functions;

/**
 * Interface {@code Functions} defines the contract for mathematical function enumerations.
 * <p>
 * It provides a method to retrieve the function's name and a static helper
 * to find a function instance by its name from an array of functions.
 * </p>
 *
 * <p>This interface is intended to be implemented by enums that represent
 * sets of mathematical functions with identifiable names.</p>
 *
 * @see MathFunctions
 * @see SymjaFunctions
 */
public interface Functions {

    /**
     * Returns the name of the function.
     *
     * @return the function name as a string
     */
    String getName();

    /**
     * Searches for a function with a given name within an array of {@code Functions}.
     * The comparison is case-insensitive.
     *
     * @param name the name to search for
     * @param functions the array of {@code Functions} to search within
     * @return the matching {@code Functions} instance if found, otherwise {@code null}
     */
    static Functions fromName(String name, Functions[] functions) {
        for (Functions f : functions) {
            if (f.getName().equalsIgnoreCase(name)) {
                return f;
            }
        }
        return null;
    }
}
