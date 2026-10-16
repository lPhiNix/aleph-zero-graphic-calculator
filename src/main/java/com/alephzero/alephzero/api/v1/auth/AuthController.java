package com.alephzero.alephzero.api.v1.auth;

import com.alephzero.alephzero.api.v1.auth.dto.AuthResponse;
import com.alephzero.alephzero.api.v1.auth.dto.LoginRequest;
import com.alephzero.alephzero.api.v1.auth.dto.RegistrationFormDto;
import com.alephzero.alephzero.core.exception.InvalidRefreshTokenException;
import com.alephzero.alephzero.core.services.auth.AuthService;
import com.alephzero.alephzero.infrastructure.security.AuthCookieService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for the stateless token-based authentication system.
 * <p>
 * The refresh token is never part of the response body: it is transported in an
 * HttpOnly cookie managed by {@link AuthCookieService}.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthCookieService authCookieService;

    /**
     * Registers a new user.
     *
     * @param request the registration payload (validated)
     * @return 201 Created on success
     */
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegistrationFormDto request) {
        authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Authenticates the user and issues a token pair. The refresh token is set in
     * an HttpOnly cookie; the access token is returned in the body.
     *
     * @param request  the login credentials
     * @param response the response (to set the refresh cookie)
     * @return 200 with the access token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                              HttpServletResponse response) {
        AuthService.AuthResult result = authService.login(request);
        authCookieService.setRefreshToken(response, result.refreshToken(), request.rememberMe());
        return ResponseEntity.ok(new AuthResponse(result.accessToken(), result.expiresInSeconds()));
    }

    /**
     * Rotates the refresh token (from the cookie) and issues a new access token.
     *
     * @param request  the request (to read the refresh cookie)
     * @param response the response (to set the rotated refresh cookie)
     * @return 200 with a fresh access token
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(HttpServletRequest request,
                                                HttpServletResponse response) {
        String refreshToken = authCookieService.readRefreshToken(request);
        if (refreshToken == null) {
            throw new InvalidRefreshTokenException();
        }

        AuthService.AuthResult result = authService.refresh(refreshToken);
        authCookieService.setRefreshToken(response, result.refreshToken(), true);
        return ResponseEntity.ok(new AuthResponse(result.accessToken(), result.expiresInSeconds()));
    }

    /**
     * Revokes the current refresh token and clears the cookie.
     *
     * @param request  the request (to read the refresh cookie)
     * @param response the response (to clear the refresh cookie)
     * @return 204 No Content
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request,
                                       HttpServletResponse response) {
        String refreshToken = authCookieService.readRefreshToken(request);
        if (refreshToken != null) {
            authService.logout(refreshToken);
        }
        authCookieService.clearRefreshToken(response);
        return ResponseEntity.noContent().build();
    }
}
