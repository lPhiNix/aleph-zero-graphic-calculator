package com.placeholder.placeholder.api.auth;

import com.placeholder.placeholder.api.auth.dto.UserLoginRequestDto;
import com.placeholder.placeholder.api.auth.dto.UserRegisterRequestDto;
import com.placeholder.placeholder.api.user.service.UserRoleService;
import com.placeholder.placeholder.api.user.service.UserService;
import com.placeholder.placeholder.db.models.User;
import com.placeholder.placeholder.db.models.UserRole;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
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
    public User registerUser(UserRegisterRequestDto request) {
        String encoded = passwordEncoder.encode(request.password());
        UserRole role = userRoleService.getDefaultRole(); // add cache.

        User user = new User();
        user.setEmail(request.email());
        user.setUsername(request.username());
        user.setPassword(encoded);
        user.setRole(role);
        user.setPublicId(UUID.randomUUID().toString());

        return userService.save(user);
    }

    public User authenticate(UserLoginRequestDto request) {
        User newUser = userService.findUserByUsername(request.identifier());

        if (!passwordEncoder.matches(request.password(), newUser.getPassword())) {
            throw new BadCredentialsException("Bad credentials");
        }

        return newUser;
    }
}

