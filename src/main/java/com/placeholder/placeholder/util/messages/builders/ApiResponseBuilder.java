package com.placeholder.placeholder.util.messages.builders;

import com.placeholder.placeholder.util.messages.dto.ApiMessage;
import com.placeholder.placeholder.util.messages.dto.MessageContent;

/**
 * A builder class for creating {@link ApiMessage} instances with custom status, code, message, path, and content.
 *
 * @param <T> the type of content, which must implement {@link MessageContent}
 */
public class ApiResponseBuilder<T extends MessageContent> {
    private int status;
    private String code;
    private String message;
    private String path;
    private T content;

    /**
     * Creates a new instance of {@code ApiResponseBuilder}.
     *
     * @param <T> the type of the response content
     * @return a new builder instance
     */
    public static <T extends MessageContent> ApiResponseBuilder<T> builder() {
        return new ApiResponseBuilder<>();
    }

    /**
     * Sets the HTTP status code.
     *
     * @param status the HTTP status code (e.g., 200, 404)
     * @return this builder instance
     */
    public ApiResponseBuilder<T> status(int status) {
        this.status = status;
        return this;
    }

    /**
     * Sets the application-specific code.
     *
     * @param code the application code (e.g., "OK", "BAD_REQUEST")
     * @return this builder instance
     */
    public ApiResponseBuilder<T> code(String code) {
        this.code = code;
        return this;
    }

    /**
     * Sets a brief message describing the response.
     *
     * @param message a short, human-readable message
     * @return this builder instance
     */
    public ApiResponseBuilder<T> message(String message) {
        this.message = message;
        return this;
    }

    /**
     * Sets the request path that generated the response.
     *
     * @param path the endpoint path (e.g., "/api/users/1")
     * @return this builder instance
     */
    public ApiResponseBuilder<T> path(String path) {
        this.path = path;
        return this;
    }

    /**
     * Sets the content of the response.
     *
     * @param content the body of the response
     * @return this builder instance
     */
    public ApiResponseBuilder<T> content(T content) {
        this.content = content;
        return this;
    }

    /**
     * Builds the {@link ApiMessage} with the configured values.
     *
     * @return a new {@link ApiMessage} instance
     */
    public ApiMessage<T> build() {
        return new ApiMessage<>(status, code, message, path, content);
    }
}