package com.placeholder.placeholder.api.util.common.messages;

import org.springframework.stereotype.Component;

/**
 * Factory class for creating API message builders.
 * <p>
 * Provides methods to create builders for API responses and error responses,
 * allowing for a fluent API style of constructing messages.
 * </p>
 */
@Component
public class ApiMessageFactory {
    /**
     * Creates a new instance of {@link ApiResponseBuilder} for constructing API responses.
     *
     * @param <T> the type of content in the API response
     * @return a new {@link ApiResponseBuilder} instance
     */
    public <T> ApiResponseBuilder<T> response(){
        return new ApiResponseBuilder<>();
    }

    /**
     * Creates a new instance of {@link ErrorResponseBuilder} for constructing error responses.
     *
     * @return a new {@link ErrorResponseBuilder} instance
     */
    public ErrorResponseBuilder error() {
        return new ErrorResponseBuilder();
    }
}
