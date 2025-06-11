package com.placeholder.placeholder.util.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.placeholder.placeholder.api.util.common.messages.ApiResponseFactory;
import com.placeholder.placeholder.api.util.common.messages.dto.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Custom implementation of {@link AccessDeniedHandler} to handle 403 Forbidden errors
 * by returning a structured JSON error response instead of the default HTML response.
 * <p>
 * This handler is invoked when an authenticated user attempts to access a resource
 * for which they do not have sufficient permissions.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;
    private final ApiResponseFactory apiResponseFactory;

    /**
     * Handles {@link AccessDeniedException} by sending a 403 Forbidden HTTP response
     * with a JSON-formatted error body.
     *
     * @param request               the current {@link HttpServletRequest}
     * @param response              the current {@link HttpServletResponse}
     * @param accessDeniedException the exception thrown when access is denied
     * @throws IOException if writing to the response output stream fails
     */
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        ResponseEntity<ErrorResponse> errorResponse = apiResponseFactory.forbidden();

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), errorResponse.getBody());
    }
}


