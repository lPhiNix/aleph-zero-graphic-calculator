package com.placeholder.placeholder.api.auth;

import com.placeholder.placeholder.api.user.service.UserService;
import com.placeholder.placeholder.db.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// TODO: Implement OpenID Connect (OIDC) endpoints for authentication and user info retrieval

@RestController
@RequiredArgsConstructor
public class OidcController {
    private final UserService userService;


    @GetMapping("/.well-known/openid-configuration")
    public Map<String, Object> openidConfig() {
        return Map.of(
                "issuer", "http://localhost:8080",
                "authorization_endpoint", "http://localhost:8080/auth/login",
                "token_endpoint", "http://localhost:8080/auth/login",
                "userinfo_endpoint", "http://localhost:8080/userinfo",
                "jwks_uri", "http://localhost:8080/jwks.json",
                "response_types_supported", new String[]{"id_token", "token"},
                "subject_types_supported", new String[]{"public"},
                "id_token_signing_alg_values_supported", new String[]{"HS256"}
        );
    }


    @GetMapping("/userinfo")
    @Transactional()
    public Map<String, Object> userInfo(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findUserByIdentifier(username);
        return Map.of(
                "sub", user.getUsername(),
                "email", user.getEmail(),
                "roles", user.getRole()
        );
    }
}