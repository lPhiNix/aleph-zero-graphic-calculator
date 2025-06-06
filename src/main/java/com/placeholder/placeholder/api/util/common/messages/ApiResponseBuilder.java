package com.placeholder.placeholder.api.util.common.messages;

import com.placeholder.placeholder.api.util.common.messages.dto.ApiResponse;
import com.placeholder.placeholder.util.config.enums.AppCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.net.URI;


public class ApiResponseBuilder<T> {
    private AppCode code;
    private String message;
    private T content; // payload
    private URI location;


    public ApiResponseBuilder(T content) {
        this.content = content;
    }

    public ApiResponseBuilder() {}

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
    public ApiResponseBuilder<T> created(URI location) {
        this.code = AppCode.CREATED;
        this.location = location;
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

        // if content is null, we return a response without body
        if (content == null) {
            ResponseEntity.BodyBuilder builder = ResponseEntity.status(code.getStatus());
            if (location != null) {
                builder.header("Location", location.toString());
            }
            return builder.build();
        }

        // if content is not null, we create a response with body
        ApiResponse<T> response = new ApiResponse<>(code, message, content);
        return ResponseEntity.status(code.getStatus()).body(response);
    }

}