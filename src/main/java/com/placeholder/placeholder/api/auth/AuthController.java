package com.placeholder.placeholder.api.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import com.placeholder.placeholder.api.auth.dto.UserLoginRequestDto;
import com.placeholder.placeholder.api.auth.dto.UserRegisterRequestDto;
import com.placeholder.placeholder.api.user.service.UserService;
import com.placeholder.placeholder.db.models.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private static final int AUTH_COOKIE_MAX_AGE = 7 * 24 * 60 * 60; // 7 days
    private static final String AUTH_COOKIE_NAME = "access_token";

    private final AuthService authService;
    private final JwtService jwtService;
    private final UserService userService;

    @Transactional
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequestDto request, HttpServletResponse response) {
        User user = authService.authenticate(request);
        String token = jwtService.generateToken(user.getUsername(), user.getRole().getName(), user.getEmail());

        // Set JWT as HttpOnly cookie
        ResponseCookie cookie = ResponseCookie.from(AUTH_COOKIE_NAME, token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(AUTH_COOKIE_MAX_AGE)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok().body("Login successful");
    }

    @Transactional
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterRequestDto request) {
        User user = authService.registerUser(request);
        return ResponseEntity.ok().body("User registered successfully");
    }
}
