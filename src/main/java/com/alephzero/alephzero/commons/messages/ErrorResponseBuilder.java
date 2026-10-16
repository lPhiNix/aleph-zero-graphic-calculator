package com.alephzero.alephzero.commons.messages;

import com.alephzero.alephzero.commons.messages.dto.error.ErrorResponse;
import com.alephzero.alephzero.commons.messages.dto.error.details.ApiErrorDetail;
import com.alephzero.alephzero.infrastructure.config.enums.AppCode;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Builder for constructing {@link ErrorResponse} (error) objects.
 * <p>
 * Provides a fluent API for setting error code, title, and details,
 * as well as chainable convenience methods for common error cases.
 * </p>
 *
 */
public class ErrorResponseBuilder {
	private AppCode code;
	private String errorTitle;
	private List<? extends ApiErrorDetail> errorDetails;

	private ErrorResponseBuilder() {
	}

	/**
	 * Creates an empty builder for manual configuration.
	 *
	 * @return a new builder instance
	 */
	public static ErrorResponseBuilder create() {
		return new ErrorResponseBuilder();
	}

	/**
	 * Configures this builder for a 400 Bad Request error.
	 *
	 * @return this builder instance for chaining
	 */
	public static ErrorResponseBuilder badRequest() {
		return create()
				.code(AppCode.BAD_REQUEST)
				.title(AppCode.BAD_REQUEST.getSimpleMessage());
	}

	/**
	 * Configures this builder for a 401 Unauthorized error.
	 *
	 * @return this builder instance for chaining
	 */
	public static ErrorResponseBuilder unauthorized() {
		return create()
				.code(AppCode.UNAUTHORIZED)
				.title(AppCode.UNAUTHORIZED.getSimpleMessage());
	}

	/**
	 * Configures this builder for a 403 Forbidden error.
	 *
	 * @return this builder instance for chaining
	 */
	public static ErrorResponseBuilder forbidden() {
		return create()
				.code(AppCode.FORBIDDEN)
				.title(AppCode.FORBIDDEN.getSimpleMessage());
	}

	/**
	 * Configures this builder for a 404 Not Found error.
	 *
	 * @return this builder instance for chaining
	 */
	public static ErrorResponseBuilder notFound() {
		return create()
				.code(AppCode.NOT_FOUND)
				.title(AppCode.NOT_FOUND.getSimpleMessage());
	}

	/**
	 * Configures this builder for a 409 Conflict error.
	 *
	 * @return this builder instance for chaining
	 */
	public static ErrorResponseBuilder conflict() {
		return create()
				.code(AppCode.CONFLICT)
				.title(AppCode.CONFLICT.getSimpleMessage());
	}

	/**
	 * Configures this builder for a 400 Validation Error.
	 *
	 * @return this builder instance for chaining
	 */
	public static ErrorResponseBuilder validation() {
		return create()
				.code(AppCode.VALIDATION_ERROR)
				.title(AppCode.VALIDATION_ERROR.getSimpleMessage());
	}

	/**
	 * Configures this builder for a 500 Internal Server Error.
	 *
	 * @return this builder instance for chaining
	 */
	public static ErrorResponseBuilder internal() {
		return create()
				.code(AppCode.INTERNAL_ERROR)
				.title(AppCode.INTERNAL_ERROR.getSimpleMessage());
	}

	/**
	 * Configures this builder for a 503 Service Unavailable error.
	 *
	 * @return this builder instance for chaining
	 */
	public static ErrorResponseBuilder serviceUnavailable() {
		return create()
				.code(AppCode.SERVICE_UNAVAILABLE)
				.title(AppCode.SERVICE_UNAVAILABLE.getSimpleMessage());
	}

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
	 * Adds a single error detail to this error response.
	 *
	 * @param detail the {@link ApiErrorDetail} to add
	 * @return this builder instance for chaining
	 */
	public ErrorResponseBuilder detail(ApiErrorDetail detail) {
		this.errorDetails = List.of(detail);
		return this;
	}

	/**
	 * Sets the error details for this error response.
	 *
	 * @param details a list of {@link ApiErrorDetail} objects
	 * @return this builder instance for chaining
	 */
	public ErrorResponseBuilder details(List<? extends ApiErrorDetail> details) {
		this.errorDetails = List.copyOf(details);
		return this;
	}

	/**
	 * Builds the {@link ResponseEntity} containing the {@link ErrorResponse} with
	 * the configured values.
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

		ErrorResponse error = new ErrorResponse(code.toString(), errorTitle, errorDetails);

		return ResponseEntity.status(code.getStatus()).body(error);
	}
}
