package com.placeholder.placeholder.api.util.common.messages;

import com.placeholder.placeholder.api.util.common.messages.dto.ApiResponse;
import com.placeholder.placeholder.util.config.enums.AppCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Builder for constructing {@link ApiResponse} (success) objects.
 * <p>
 * This builder provides a fluent API for setting response code, message, and content,
 * and for building a {@link ResponseEntity} containing the API response.
 * </p>
 */
@RequiredArgsConstructor
public class ApiResponseBuilder<T> {
    private AppCode code;
    private String message;
    private final T content;

    /**
     * Sets the application response code for this response.
     *
     * @param code the {@link AppCode} representing the response status
     * @return this builder instance for chaining
     */
    public ApiResponseBuilder<T> code(AppCode code) {
        this.code = code;
        return this;
    }

    /**
     * Sets the message for this response.
     *
     * @param message the message to include in the response
     * @return this builder instance for chaining
     */
    public ApiResponseBuilder<T> message(String message) {
        this.message = message;
        return this;
    }

//    /**
//     * Sets the content (payload) for this response.
//     *
//     * @param content the content to include in the response
//     * @return this builder instance for chaining
//     */
//    public ApiResponseBuilder<T> content(T content) {
//        this.content = content;
//        return this;
//    }

    /**
     * Configures this builder for an HTTP 200 OK response with the given content.
     *
     * @return this builder instance for chaining
     */
    public ApiResponseBuilder<T> ok() {
        this.code = AppCode.OK;
        return this;
    }

    /**
     * Configures this builder for an HTTP 201 Created response with the given content.
     *
     * @return this builder instance for chaining
     */
    public ApiResponseBuilder<T> created() {
        this.code = AppCode.CREATED;
        return this;
    }

    /**
     * Configures this builder for an HTTP 202 Accepted response with the given content.
     *
     * @return this builder instance for chaining
     */
    public ApiResponseBuilder<T> accepted() {
        this.code = AppCode.ACCEPTED;
        return this;
    }

    /**
     * Configures this builder for an HTTP 204 No Content response.
     *
     * @return this builder instance for chaining
     */
    public ApiResponseBuilder<T> noContent() {
        this.code = AppCode.NO_CONTENT;
        return this;
    }

    /**
     * Builds the {@link ResponseEntity} containing the {@link ApiResponse} with the configured values.
     * <p>
     * If the message is not set, the default message from the {@link AppCode} is used.
     * </p>
     *
     * @return a {@link ResponseEntity} containing the API response
     * @throws IllegalStateException if the response code is not set
     */
    public ResponseEntity<ApiResponse<T>> build() {
        if (code == null) {
            throw new IllegalStateException("Response code must be set before building the response");
        }
        if (message == null) {
            message = code.getSimpleMessage();
        }
        ApiResponse<T> response = new ApiResponse<>(code, message, content);
        ResponseEntity<ApiResponse<T>> entity = ResponseEntity.status(code.getStatus()).body(response);
        reset();

        return entity;
    }

    private void reset() {
        this.code = null;
        this.message = null;
    }
}