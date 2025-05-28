package com.placeholder.placeholder.util.config;

import com.placeholder.placeholder.api.user.service.UserService;
import com.placeholder.placeholder.api.util.common.auth.TokenClaims;
import com.placeholder.placeholder.db.models.User;
import com.placeholder.placeholder.util.CustomAuthenticationEntryPoint;
import com.placeholder.placeholder.api.auth.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserService userService;
    private final CustomAuthenticationEntryPoint entryPoint;

    private final Set<String> allowedEndpoints;
    private final Set<String> allowedStaticEndpoints;

    /**
     * This filter checks for a JWT token in the Authorization header of the request.
     * If a valid token is found, it extracts the username and authenticates the user.
     * If the token is invalid or not present, it allows the request to proceed without authentication.
     *
     * @param req   The HTTP request
     * @param res   The HTTP response
     * @param chain The filter chain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String path = req.getRequestURI();

        // Skip processing for public or static endpoints
        if (allowedEndpoints.stream().anyMatch(path::startsWith) ||
                allowedStaticEndpoints.stream().anyMatch(path::startsWith)) {
            chain.doFilter(req, res);
            return;
        }

        String token = extractToken(req);
        if (token == null) {
            chain.doFilter(req, res);
            return;
        }

        try {
            String username = extractIdentifier(token);
            if (username == null || SecurityContextHolder.getContext().getAuthentication() != null) {
                chain.doFilter(req, res);
                return;
            }
            authenticateUser(req, username);
        } catch (Exception e) {
            entryPoint.commence(req, res, new BadCredentialsException("Invalid JWT token: " + e.getMessage()));
            return;
        }
        chain.doFilter(req, res);
    }

    private String extractToken(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private String extractIdentifier(String token) {
        return Optional.of(jwtService.parseClaims(token))
                .flatMap(claims -> claims.getPreferredUsername().or(claims::getEmail))
                .orElseThrow(() -> new BadCredentialsException("JWT token does not contain a valid identifier"));
    }


    /**
     * Authenticates the user based on the identifier extracted from the JWT token.
     * If the user is found, it sets the authentication in the SecurityContext.
     *
     * @param req      The HTTP request
     * @param identifier The identifier (email or username) extracted from the JWT token
     */
    private void authenticateUser(HttpServletRequest req, String identifier) {
        User user = userService.findUserByIdentifier(identifier); // This may throw
        if (user != null) {
            var authorities = List.of(new SimpleGrantedAuthority(user.getRole().getName()));
            // Try to not authenticate the user with the JPA OBJECT, impossible challenge.
            var auth = new UsernamePasswordAuthenticationToken(user.getUsername(), null, authorities);
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
    }
}