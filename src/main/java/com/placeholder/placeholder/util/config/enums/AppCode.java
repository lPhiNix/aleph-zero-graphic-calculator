package com.placeholder.placeholder.util.config.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Enum of application-specific response codes, each associated with an HTTP status and a default message.
 * <p>
 * This enum provides a unified set of codes representing various outcomes of API operations.
 * Each constant defines:
 * <ul>
 *   <li>{@link #status} - the HTTP status to be returned</li>
 *   <li>{@link #simpleMessage} - a human-readable, default message for the code</li>
 * </ul>
 */
@Getter
public enum AppCode {

    // --- Success responses ---

    /**
     * Operation completed successfully.
     * <p>
     * Maps to HTTP 200 OK.
     */
    OK(HttpStatus.OK, "Operation Successful"),

    /**
     * Resource created successfully.
     * <p>
     * Maps to HTTP 201 Created.
     */
    CREATED(HttpStatus.CREATED, "Resource created successfully"),

    /**
     * Request accepted.
     * Maps to HTTP 202 Accepted.
     */
    ACCEPTED(HttpStatus.ACCEPTED, "Resource accepted"),

    /**
     * Request succeeded but no content to return.
     * <p>
     * Maps to HTTP 204 No Content.
     */
    NO_CONTENT(HttpStatus.NO_CONTENT, "No Content available"),

    // --- Client errors ---

    /**
     * The request could not be understood or was missing required parameters.
     * <p>
     * Maps to HTTP 400 Bad Request.
     */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad request"),

    /**
     * The server timed out waiting for the request.
     * <p>
     * Maps to HTTP 408 Request Timeout.
     */
    TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "Request timeout"),

    /**
     * Authentication failed or user does not have permissions for the desired action.
     * <p>
     * Maps to HTTP 401 Unauthorized.
     */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized"),

    /**
     * Access to the resource is forbidden.
     * <p>
     * Maps to HTTP 403 Forbidden.
     */
    FORBIDDEN(HttpStatus.FORBIDDEN, "Access forbidden"),

    /**
     * The specified resource could not be found.
     * <p>
     * Maps to HTTP 404 Not Found.
     */
    NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not found"),

    /**
     * A conflict occurred, such as an edit conflict between multiple simultaneous updates.
     * <p>
     * Maps to HTTP 409 Conflict.
     */
    CONFLICT(HttpStatus.CONFLICT, "Resource conflict"),

    /**
     * The request method is not supported for the specified resource.
     * <p>
     * Maps to HTTP 405 Method Not Allowed.
     */
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "Method not allowed"),

    /**
     * The resource cannot generate content acceptable according to the Accept headers.
     * <p>
     * Maps to HTTP 406 Not Acceptable.
     */
    NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE, "Not acceptable"),

    /**
     * The request entity has a media type which the server or resource does not support.
     * <p>
     * Maps to HTTP 415 Unsupported Media Type.
     */
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported media type"),

    // --- Validation and application-specific errors ---

    /**
     * One or more validation errors occurred.
     * <p>
     * Typically used when request payload fails business validation rules.
     * Maps to HTTP 400 Bad Request.
     */
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Validation error"),

    /**
     * The requested entity does not exist in the system.
     * <p>
     * Maps to HTTP 404 Not Found.
     */
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "Entity not found"),

    /**
     * Attempted to create an entity that already exists.
     * <p>
     * Maps to HTTP 409 Conflict.
     */
    DUPLICATE_ENTITY(HttpStatus.CONFLICT, "Duplicate entity"),

    // --- Server errors ---

    /**
     * An unexpected server error occurred.
     * <p>
     * Maps to HTTP 500 Internal Server Error.
     */
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),

    /**
     * The server is currently unable to handle the request due to maintenance or overload.
     * <p>
     * Maps to HTTP 503 Service Unavailable.
     */
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "Service unavailable");

    /**
     * The HTTP status associated with this application code.
     * -- GETTER --
     *  Returns the HTTP status code associated with this AppCode.
     *
     * @return the HTTP status

     */
    private final HttpStatus status;

    /**
     * A simple, default human-readable message for this code.
     * -- GETTER --
     *  Returns the default human-readable message for this AppCode.
     *
     * @return the simple message

     */
    private final String simpleMessage;

    /**
     * Constructs an AppCode with the given HTTP status and default message.
     *
     * @param httpStatus the HTTP status to return
     * @param simpleMessage the default human-readable message
     */
    AppCode(HttpStatus httpStatus, String simpleMessage) {
        this.status = httpStatus;
        this.simpleMessage = simpleMessage;
    }

    /**
     * Returns the name of this enum constant, as its string value.
     *
     * @return the enum constant name
     */
    public String value() {
        return name();
    }

}

