package com.placeholder.placeholder.api.util.common.messages;

import com.placeholder.placeholder.api.util.common.messages.dto.ApiResponse;
import com.placeholder.placeholder.api.util.common.messages.dto.content.MessageContent;
import com.placeholder.placeholder.api.util.common.messages.dto.content.responses.EmptyContentResponse;
import com.placeholder.placeholder.api.util.common.messages.dto.error.ErrorResponse;
import com.placeholder.placeholder.api.util.common.messages.dto.error.details.ApiErrorDetail;
import com.placeholder.placeholder.api.util.common.messages.dto.error.details.ValidationErrorDetail;
import com.placeholder.placeholder.util.enums.AppCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Factory class responsible for constructing standardized {@link ApiResponse} and {@link ErrorResponse} objects for Api responses in controllers
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
     * @param message       A message to be included in the response body.
     * @param content       Content of the response, which can be any class
     *                      implementing {@link MessageContent}.
     * @param <T>           The type of the message content.
     * @return A {@link ResponseEntity} containing the constructed {@link ApiResponse}.
     */
    private <T extends MessageContent> ResponseEntity<ApiResponse<T>> build(AppCode code, String message, T content) {
        ApiResponse<T> response = new ApiResponse<>(
                code,
                message,
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
     * Constructs a successful (HTTP 200 OK) API response with the given content.
     *
     * <p>Use this method when the request is successfully processed and the response body
     * contains the result of the operation.</p>
     *
     * @param content The data to be included in the successful response, which can be any response DTO.
     * @param <T>     The type of the message content. It must extend {@link MessageContent}.
     * @return A {@link ResponseEntity} representing a successful response (HTTP 200 OK)
     *         with the provided content.
     */
    public <T extends MessageContent> ResponseEntity<ApiResponse<T>> ok(T content) {
        return build(AppCode.OK, AppCode.OK.getSimpleMessage(), content);
    }

    /**
     * Constructs a successful (HTTP 201 Created) API response, including the location
     * of the newly created resource.
     *
     * <p>Use this method when a new resource is created, and the response should include
     * the URI where the resource can be accessed.</p>
     *
     * @param content  The data to be included in the successful response.
     * @param location The URI where the newly created resource can be accessed.
     * @param <T>      The type of the message content. It must extend {@link MessageContent}.
     * @return A {@link ResponseEntity} representing a successful response (HTTP 201 Created)
     *         with the content and the location header.
     */
    public <T extends MessageContent> ResponseEntity<ApiResponse<T>> created(T content, URI location) {
        AppCode code = AppCode.CREATED;
        return ResponseEntity.created(location)
                .body(new ApiResponse<>(code, code.getSimpleMessage(), requestFactory.getObject().getRequestURI(), content));
    }

    /**
     * Constructs a successful (HTTP 202 Accepted) API response indicating that the request
     * has been accepted for processing, but the processing is not yet complete.
     *
     * <p>Use this method when the request has been accepted, but the processing is still ongoing.</p>
     *
     * @param content The data to be included in the response body, which may represent
     *                the current status or partial result of the operation.
     * @param <T>     The type of the message content. It must extend {@link MessageContent}.
     * @return A {@link ResponseEntity} representing a successful response (HTTP 202 Accepted)
     *         with the provided content.
     */
    public <T extends MessageContent> ResponseEntity<ApiResponse<T>> accepted(T content) {
        return build(AppCode.ACCEPTED, AppCode.ACCEPTED.getSimpleMessage(), content);
    }

    /**
     * Constructs a successful (HTTP 202 Accepted) API response indicating that the request
     * has been accepted for processing, but the processing is not yet complete.
     *
     * <p>Use this method when the request has been accepted, but the processing is still ongoing.</p>

     * @return A {@link ResponseEntity} representing a successful response (HTTP 202 Accepted)
     *         without content.
     */
    public ResponseEntity<ApiResponse<EmptyContentResponse>> accepted() {
        return build(AppCode.ACCEPTED, AppCode.ACCEPTED.getSimpleMessage(), new EmptyContentResponse());
    }

    /**
     * Constructs a successful (HTTP 204 No Content) API response indicating that the request
     * has been successfully processed, but there is no content to return.
     *
     * <p>Use this method when the operation completes successfully, but no data is returned
     * in the response body.</p>
     *
     * @param content The data to be included in the response body, if necessary.
     * @param <T>     The type of the message content. It must extend {@link MessageContent}.
     * @return A {@link ResponseEntity} representing a successful response (HTTP 204 No Content)
     *         with the provided content.
     */
    public <T extends MessageContent> ResponseEntity<ApiResponse<T>> noContent(T content) {
        return build(AppCode.NO_CONTENT, AppCode.NO_CONTENT.getSimpleMessage(), content);
    }

    /**
     * Constructs a successful (HTTP 204 No Content) API response indicating that the request
     * has been successfully processed, but there is no content to return.
     *
     * <p>Use this method when the operation completes successfully, but no data is returned
     * in the response body. This version does not require any content.</p>
     *
     * @return A {@link ResponseEntity} representing a successful response (HTTP 204 No Content)
     */
    public ResponseEntity<ApiResponse<EmptyContentResponse>> noContent() {
        return build(AppCode.NO_CONTENT, AppCode.NO_CONTENT.getSimpleMessage(), new EmptyContentResponse());
    }



    // == ERRORS: GENERIC ==

    /**
     * Constructs a generic error response with a given HTTP code, title, summary, and details.
     *
     * <p>Use this method when you want to return a detailed error response including a summary
     * and a list of specific error details.</p>
     *
     * @param code    The {@link AppCode} representing the specific application error and HTTP status.
     * @param title   A short, human-readable title describing the error.
     * @param summary A brief explanation or summary of the error.
     * @param details A list of {@link ApiErrorDetail} objects providing additional detail.
     * @return A {@link ResponseEntity} representing the error response with the appropriate HTTP status.
     */
    public ResponseEntity<ErrorResponse> error(AppCode code, String title, String summary, List<ApiErrorDetail> details) {
        ErrorResponse error = buildError(title, summary, details);
        return ResponseEntity.status(code.getStatus()).body(error);
    }

    /**
     * Constructs a generic error response with a given HTTP code, title, and detailed error list.
     *
     * <p>Use this method when the error does not require a summary, but includes multiple details.</p>
     *
     * @param code    The {@link AppCode} representing the specific application error and HTTP status.
     * @param title   A short, human-readable title describing the error.
     * @param details A list of {@link ApiErrorDetail} objects providing detailed error context.
     * @return A {@link ResponseEntity} representing the error response with the appropriate HTTP status.
     */
    public ResponseEntity<ErrorResponse> error(AppCode code, String title, List<ApiErrorDetail> details) {
        ErrorResponse error = buildError(title, null, details);
        return ResponseEntity.status(code.getStatus()).body(error);
    }

    /**
     * Constructs a generic error response with a given HTTP code, title, and summary.
     *
     * <p>Use this method when you need to provide a concise explanation without error details.</p>
     *
     * @param code    The {@link AppCode} representing the specific application error and HTTP status.
     * @param title   A short, human-readable title describing the error.
     * @param summary A brief explanation or summary of the error.
     * @return A {@link ResponseEntity} representing the error response with the appropriate HTTP status.
     */
    public ResponseEntity<ErrorResponse> error(AppCode code, String title, String summary) {
        ErrorResponse error = buildError(title, summary, null);
        return ResponseEntity.status(code.getStatus()).body(error);
    }

// == ERRORS: CONCRETE ==

    /**
     * Constructs a validation error response (HTTP 400 BAD REQUEST.
     *
     * <p>Use this method when a user-provided input fails validation checks and you want to return
     * field-specific validation messages.</p>
     *
     * @param title   A short, human-readable title describing the validation error.
     * @param details A list of {@link ValidationErrorDetail} objects providing details about the validation issues.
     * @return A {@link ResponseEntity} representing a validation error response with HTTP status 422.
     */
    public ResponseEntity<ErrorResponse> validationError(String title, List<ValidationErrorDetail> details) {
        AppCode code = AppCode.VALIDATION_ERROR;
        String summary = getErrorSummary(details);
        ErrorResponse error = buildError(title, summary, details);
        return ResponseEntity.status(code.getStatus()).body(error);
    }
}
