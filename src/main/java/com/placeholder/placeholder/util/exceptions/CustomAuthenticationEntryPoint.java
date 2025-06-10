package com.placeholder.placeholder.util.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.placeholder.placeholder.api.util.common.messages.ApiResponseFactory;
import com.placeholder.placeholder.api.util.common.messages.dto.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Custom implementation of {@link AuthenticationEntryPoint} to handle unauthorized access
 * attempts by returning a structured JSON response instead of the default login page or
 * error HTML.
 *
 * <p>
 * This entry point is triggered when a user tries to access a secured endpoint
 * without being authenticated (HTTP 401 Unauthorized).
 * </p>
 */
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationEntryPoint.class);
    private final ObjectMapper objectMapper;
    private final ApiResponseFactory apiResponseFactory;

    /**
     * Handles unauthenticated access attempts by returning a 401 Unauthorized response
     * with a JSON error body.
     *
     * @param request       the current {@link HttpServletRequest}
     * @param response      the current {@link HttpServletResponse}
     * @param authException the exception that caused the authentication to fail
     * @throws IOException if writing the response fails
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        ResponseEntity<ErrorResponse> errorResponse = apiResponseFactory.unauthorized();
        logger.warn("Unauthorized access attempt: {}", authException.getMessage(), authException);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), errorResponse.getBody());
    }
}


