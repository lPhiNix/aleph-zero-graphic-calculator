package com.placeholder.placeholder.api.util.common.messages;

import com.placeholder.placeholder.api.util.common.messages.dto.ApiResponse;
import com.placeholder.placeholder.api.util.common.messages.dto.content.MessageContent;
import com.placeholder.placeholder.api.util.common.messages.dto.error.ErrorResponse;
import com.placeholder.placeholder.api.util.common.messages.dto.error.details.ApiErrorDetail;
import com.placeholder.placeholder.api.util.common.messages.dto.error.details.ValidationErrorDetail;
import com.placeholder.placeholder.util.enums.AppCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Factory class responsible for constructing standardized {@link ApiResponse} objects for Api responses in controllers
 * and exception handlers.
 * <p>
 * This factory promotes consistency in response structure and simplifies the creation of both successful
 * and error responses
 * </p>
 */
@Component
public class ApiResponseFactory {

    private final ObjectFactory<HttpServletRequest> requestFactory;

    /**
     * Constructor for {@code ApiResponseFactory}.
     *
     * @param requestFactory An {@link ObjectFactory} that provides instances of {@link HttpServletRequest}.
     *                       This is used to obtain the request URI for inclusion in the API response.
     *                       Spring's {@code ObjectFactory} is used for lazy retrieval of the request object,
     *                       avoiding potential issues in non-request-scoped contexts.
     */
    @Autowired
    public ApiResponseFactory(ObjectFactory<HttpServletRequest> requestFactory) {
        this.requestFactory = requestFactory;
    }

    /**
     * A generic private method to construct an {@link ApiResponse} with the given parameters.
     * This method encapsulates the common logic for building both success and error responses.
     *
     * @param code          The {@link AppCode} representing the status of the response (e.g., OK, ERROR).
     * @param headerMessage A message to be included in the response header.
     * @param content       Content of the response, which can be any class
     *                      implementing {@link MessageContent}.
     * @param <T>           The type of the message content.
     * @return A {@link ResponseEntity} containing the constructed {@link ApiResponse}.
     */
    private <T extends MessageContent> ResponseEntity<ApiResponse<T>> build(AppCode code, String headerMessage, T content) {
        ApiResponse<T> response = new ApiResponse<>(
                code,
                headerMessage,
                requestFactory.getObject().getRequestURI(), // Gets the request URI lazily.
                content
        );
        return ResponseEntity.status(code.getStatus()).body(response);
    }

    /**
     * A private helper method to construct an {@link ErrorResponse} object.
     *
     * @param title   The title of the error.
     * @param summary A summary description of the error.
     * @param details A list of {@link ApiErrorDetail} providing specific details about the error(s).
     *                If {@code null}, an empty list is used.
     * @return An {@link ErrorResponse} object.
     */
    private ErrorResponse buildError(String title, String summary, List<? extends ApiErrorDetail> details) {
        List<ApiErrorDetail> errors = (details != null) ? List.copyOf(details) : Collections.emptyList();
        return new ErrorResponse(title, summary, errors);
    }

    /**
     * Generates a summary message from a list of {@link ValidationErrorDetail}.  This is
     * used to provide a concise overview of validation errors.
     *
     * @param details A list of {@link ValidationErrorDetail} representing validation errors.
     * @return A formatted string summarizing the number of errors and the categories of errors.
     */
    private String getErrorSummary(List<ValidationErrorDetail> details) {
        String categories = details.stream()
                .map(ValidationErrorDetail::category)
                .filter(Objects::nonNull) //handles null categories.
                .map(Enum::name)
                .distinct()
                .collect(Collectors.joining(", ", "{", "}"));
        return String.format("%d errors found when processing request with categories: %s", details.size(), categories);
    }

    /**
     * Constructs a successful API response with the given content.
     *
     * @param content The data to be included in the successful response.
     * @param <T>     The type of the message content.
     * @return A {@link ResponseEntity} representing a successful response (HTTP 200 OK).
     */
    public <T extends MessageContent> ResponseEntity<ApiResponse<T>> ok(T content) {
        return build(AppCode.OK, AppCode.OK.getSimpleMessage(), content);
    }

    // == ERRORS: GENERIC ==

    /**
     * Constructs an error response with detailed error information.
     *
     * @param code    The {@link AppCode} representing the error code.
     * @param title   The title of the error.
     * @param summary A summary description of the error.
     * @param details A list of {@link ApiErrorDetail} providing specific details about the error.
     * @return A {@link ResponseEntity} representing an error response.
     */
    public ResponseEntity<ErrorResponse> error(AppCode code, String title, String summary, List<ApiErrorDetail> details) {
        ErrorResponse error = buildError(title, summary, details);
        return ResponseEntity.status(code.getStatus()).body(error);
    }

    /**
     * Constructs an error response with detailed error information, omitting the summary.
     *
     * @param code    The {@link AppCode} representing the error code.
     * @param title   The title of the error.
     * @param details A list of {@link ApiErrorDetail} providing specific details about the error.
     * @return A {@link ResponseEntity} representing an error response.
     */
    public ResponseEntity<ErrorResponse> error(AppCode code, String title, List<ApiErrorDetail> details) {
        ErrorResponse error = buildError(title, null, details);
        return ResponseEntity.status(code.getStatus()).body(error);
    }

    /**
     * Constructs a simple error response with just a title and summary.
     *
     * @param code    The {@link AppCode} representing the error code.
     * @param title   The title of the error.
     * @param summary A summary description of the error.
     * @return A {@link ResponseEntity} representing an error response.
     */
    public ResponseEntity<ErrorResponse> error(AppCode code, String title, String summary) {
        ErrorResponse error = buildError(title, summary, null);
        return ResponseEntity.status(code.getStatus()).body(error);
    }

    // == ERRORS: CONCRETE ==

    /**
     * Constructs a predefined validation error response.
     *
     * @param title   The title of the validation error.
     * @param details A list of {@link ValidationErrorDetail} providing specific details about
     *                the validation errors.
     * @return A {@link ResponseEntity} representing a validation error response (HTTP 400 Bad Request).
     */
    public ResponseEntity<ErrorResponse> validationError(String title, List<ValidationErrorDetail> details) {
        AppCode code = AppCode.VALIDATION_ERROR;
        String summary = getErrorSummary(details);
        ErrorResponse error = buildError(title, summary, details);
        return ResponseEntity.status(code.getStatus()).body(error);
    }
}
