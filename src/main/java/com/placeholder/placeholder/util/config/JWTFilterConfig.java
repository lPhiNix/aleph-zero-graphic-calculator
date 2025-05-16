package com.placeholder.placeholder.util.config;

import com.placeholder.placeholder.api.util.common.auth.UserDetailsService;
import com.placeholder.placeholder.api.util.common.auth.base.JwtFacade;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * TEST AN UNHOLY AMOUNT OF TIMES THIS FUCKING CLASS.
 */
@Service
@RequiredArgsConstructor
public class JWTFilterConfig extends OncePerRequestFilter {

    private final JwtFacade jwtService;
    private final UserDetailsService userDetailsService;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    private final static Logger logger = LoggerFactory.getLogger(JWTFilterConfig.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            unauthorized(request, response, "Authorization header is invalid or missing");
            return;
        }

        final String token = authHeader.substring(7);

        try {
            if (!jwtService.isValidToken(token)) {
                unauthorized(request, response, "Invalid JWT token");
                return;
            }

            final String username = jwtService.extractSubject(token);
            if (username == null) {
                unauthorized(request, response, "JWT token does not contain a subject");
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                logger.warn("User already authenticated");
                filterChain.doFilter(request, response);
                return;
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            logger.error("JWT filter error: {}", e.getMessage());
            authenticationEntryPoint.commence(request, response,
                    new BadCredentialsException("Authentication failed", e));
        }
    }

    private void unauthorized(HttpServletRequest req, HttpServletResponse res, String msg)
            throws IOException, ServletException {
        authenticationEntryPoint.commence(req, res, new BadCredentialsException(msg));
    }

}

