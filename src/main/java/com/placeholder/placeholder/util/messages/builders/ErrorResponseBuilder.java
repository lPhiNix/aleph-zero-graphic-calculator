package com.placeholder.placeholder.util.messages.builders;

import com.placeholder.placeholder.util.messages.dto.error.details.ValidationErrorDetail;
import com.placeholder.placeholder.util.messages.dto.error.ErrorResponse;

import java.util.List;

/**
 * A builder class for constructing {@link ErrorResponse} instances.
 * Allows customization of simple and detailed error messages as well as a list of error details.
 */
public class ErrorResponseBuilder {
    private String detailedMessage;
    private String simpleMessage;
    private List<ValidationErrorDetail> errors;

    /**
     * Creates a new instance of {@code ErrorResponseBuilder}.
     *
     * @return a new builder instance
     */
    public static ErrorResponseBuilder builder() {
        return new ErrorResponseBuilder();
    }

    /**
     * Sets the detailed message, typically used for logging or debugging purposes.
     *
     * @param detailedMessage the detailed message to include in the error response
     * @return this builder instance
     */
    public ErrorResponseBuilder detailedMessage(String detailedMessage) {
        this.detailedMessage = detailedMessage;
        return this;
    }

    /**
     * Sets the simple message, typically a user-friendly summary of the error.
     *
     * @param simpleMessage the simple message to include in the error response
     * @return this builder instance
     */
    public ErrorResponseBuilder simpleMessage(String simpleMessage) {
        this.simpleMessage = simpleMessage;
        return this;
    }

    /**
     * Sets a list of specific error details, such as field validation errors.
     *
     * @param errors a list of {@link ValidationErrorDetail} objects
     * @return this builder instance
     */
    public ErrorResponseBuilder errors(List<ValidationErrorDetail> errors) {
        this.errors = errors;
        return this;
    }

    /**
     * Builds and returns an {@link ErrorResponse} instance with the configured values.
     *
     * @return a new {@link ErrorResponse}
     */
    public ErrorResponse build() {
        return new ErrorResponse(detailedMessage, simpleMessage, errors);
    }
}