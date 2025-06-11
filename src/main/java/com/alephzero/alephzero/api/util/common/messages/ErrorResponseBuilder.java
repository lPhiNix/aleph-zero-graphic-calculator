package com.alephzero.alephzero.api.util.common.messages;

import com.alephzero.alephzero.api.util.common.messages.dto.error.ErrorResponse;
import com.alephzero.alephzero.api.util.common.messages.dto.error.details.ApiErrorDetail;
import com.alephzero.alephzero.util.config.enums.AppCode;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for constructing {@link ErrorResponse} (error) objects.
 * <p>
 * Provides a fluent API for setting error code, title, summary, and details,
 * as well as chainable convenience methods for common error cases.
 * </p>
 *
 */
public class ErrorResponseBuilder {
    private AppCode code;
    private String errorTitle;
    private String errorSummary;
    private final List<ApiErrorDetail> errorDetails = new ArrayList<>();

    /**
     * Sets the application error code for this error response.
     *
     * @param code the {@link AppCode} representing the error status
     * @return this builder instance for chaining
     */
    public ErrorResponseBuilder code(AppCode code) {
        this.code = code;
        return this;
    }

    /**
     * Sets the error title for this error response.
     *
     * @param errorTitle the title describing the error
     * @return this builder instance for chaining
     */
    public ErrorResponseBuilder title(String errorTitle) {
        this.errorTitle = errorTitle;
        return this;
    }

    /**
     * Sets the error summary for this error response.
     *
     * @param errorSummary a brief explanation of the error
     * @return this builder instance for chaining
     */
    public ErrorResponseBuilder summary(String errorSummary) {
        this.errorSummary = errorSummary;
        return this;
    }

    /**
     * Adds a single error detail to this error response.
     *
     * @param detail the {@link ApiErrorDetail} to add
     * @return this builder instance for chaining
     */
    public ErrorResponseBuilder detail(ApiErrorDetail detail) {
        this.errorDetails.add(detail);
        return this;
    }

    /**
     * Sets the error details for this error response.
     *
     * @param details a list of {@link ApiErrorDetail} objects
     * @return this builder instance for chaining
     */
    public ErrorResponseBuilder details(List<? extends ApiErrorDetail> details) {
        this.errorDetails.clear();
        this.errorDetails.addAll(details);
        return this;
    }

    // --- Predefined error cases ---

    /**
     * Configures this builder for a 400 Bad Request error.
     *
     * @param summary a brief explanation of the error
     * @return this builder instance for chaining
     */
    public ErrorResponseBuilder badRequest(String summary) {
        this.code = AppCode.BAD_REQUEST;
        this.errorTitle = AppCode.BAD_REQUEST.getSimpleMessage();
        this.errorSummary = summary;
        return this;
    }

    /**
     * Configures this builder for a 401 Unauthorized error.
     *
     * @param summary a brief explanation of the error
     * @return this builder instance for chaining
     */
    public ErrorResponseBuilder unauthorized(String summary) {
        this.code = AppCode.UNAUTHORIZED;
        this.errorTitle = AppCode.UNAUTHORIZED.getSimpleMessage();
        this.errorSummary = summary;
        return this;
    }

    /**
     * Configures this builder for a 403 Forbidden error.
     *
     * @param summary a brief explanation of the error
     * @return this builder instance for chaining
     */
    public ErrorResponseBuilder forbidden(String summary) {
        this.code = AppCode.FORBIDDEN;
        this.errorTitle = AppCode.FORBIDDEN.getSimpleMessage();
        this.errorSummary = summary;
        return this;
    }

    /**
     * Configures this builder for a 404 Not Found error.
     *
     * @param summary a brief explanation of the error
     * @return this builder instance for chaining
     */
    public ErrorResponseBuilder notFound(String summary) {
        this.code = AppCode.NOT_FOUND;
        this.errorTitle = AppCode.NOT_FOUND.getSimpleMessage();
        this.errorSummary = summary;
        return this;
    }

    /**
     * Configures this builder for a 409 Conflict error.
     *
     * @param summary a brief explanation of the error
     * @return this builder instance for chaining
     */
    public ErrorResponseBuilder conflict(String summary) {
        this.code = AppCode.CONFLICT;
        this.errorTitle = AppCode.CONFLICT.getSimpleMessage();
        this.errorSummary = summary;
        return this;
    }

    /**
     * Configures this builder for a 400 Validation Error.
     *
     * @param summary a brief explanation of the validation error
     * @return this builder instance for chaining
     */
    public ErrorResponseBuilder validation(String summary) {
        this.code = AppCode.VALIDATION_ERROR;
        this.errorTitle = AppCode.VALIDATION_ERROR.getSimpleMessage();
        this.errorSummary = summary;
        return this;
    }

    /**
     * Configures this builder for a 500 Internal Server Error.
     *
     * @param summary a brief explanation of the error
     * @return this builder instance for chaining
     */
    public ErrorResponseBuilder internal(String summary) {
        this.code = AppCode.INTERNAL_ERROR;
        this.errorTitle = AppCode.INTERNAL_ERROR.getSimpleMessage();
        this.errorSummary = summary;
        return this;
    }

    /**
     * Configures this builder for a 503 Service Unavailable error.
     *
     * @param summary a brief explanation of the error
     * @return this builder instance for chaining
     */
    public ErrorResponseBuilder serviceUnavailable(String summary) {
        this.code = AppCode.SERVICE_UNAVAILABLE;
        this.errorTitle = AppCode.SERVICE_UNAVAILABLE.getSimpleMessage();
        this.errorSummary = summary;
        return this;
    }

    /**
     * Builds the {@link ResponseEntity} containing the {@link ErrorResponse} with the configured values.
     *
     * @return a {@link ResponseEntity} containing the error response
     * @throws IllegalStateException if the error code is not set
     */
    public ResponseEntity<ErrorResponse> build() {
        if (code == null) {
            throw new IllegalStateException("Error code must be set before building the response");
        }

        if (errorTitle == null) {
            errorTitle = code.getSimpleMessage();
        }

        ErrorResponse error = new ErrorResponse(errorTitle, errorSummary, errorDetails);
        ResponseEntity<ErrorResponse> response = ResponseEntity.status(code.getStatus()).body(error);

        // Reset state for reuse
        reset();

        return response;
    }

    private void reset() {
        this.code = null;
        this.errorTitle = null;
        this.errorSummary = null;
        this.errorDetails.clear();
    }
}