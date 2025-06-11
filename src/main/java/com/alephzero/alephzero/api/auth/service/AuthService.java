package com.alephzero.alephzero.api.auth.service;

import com.alephzero.alephzero.api.auth.dto.RegistrationFormDto;
import com.alephzero.alephzero.api.user.service.UserRoleService;
import com.alephzero.alephzero.api.user.service.UserService;
import com.alephzero.alephzero.db.models.User;
import com.alephzero.alephzero.db.models.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service for handling user authentication-related operations.
 *
 * <p>This service provides methods for user registration and other authentication-related tasks.
 * It interacts with the {@link UserService} and {@link UserRoleService} to manage user data and roles.
 *
 * <ul>
 *   <li>Registers new users with encoded passwords and default roles.</li>
 *   <li>Ensures transactional integrity during registration.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;

    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registers a new user with the provided registration details.
     *
     * <p>This method encodes the user's password, assigns a default role, and saves the user to the database.
     * It ensures that the operation is performed within a transaction to maintain data integrity.
     *
     * @param request The registration details containing email, username, and password.
     */
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

