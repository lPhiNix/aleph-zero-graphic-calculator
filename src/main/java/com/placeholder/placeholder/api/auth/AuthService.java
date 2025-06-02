package com.placeholder.placeholder.api.auth;

import com.placeholder.placeholder.api.auth.dto.RegistrationFormDto;
import com.placeholder.placeholder.api.user.service.UserRoleService;
import com.placeholder.placeholder.api.user.service.UserService;
import com.placeholder.placeholder.db.models.User;
import com.placeholder.placeholder.db.models.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerUser(RegistrationFormDto request) {
        String encoded = passwordEncoder.encode(request.password());
        UserRole role = userRoleService.getDefaultRole();

        User user = new User();
        user.setEmail(request.email());
        user.setUsername(request.username());
        user.setPassword(encoded);
        user.setRole(role);
        user.setPublicId(UUID.randomUUID().toString());

        userService.save(user);
    }
}

