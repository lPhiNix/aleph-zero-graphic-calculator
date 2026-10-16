package com.alephzero.alephzero.core.services.auth;

import com.alephzero.alephzero.api.v1.auth.dto.LoginRequest;
import com.alephzero.alephzero.api.v1.auth.dto.RegistrationFormDto;
import com.alephzero.alephzero.core.exception.InvalidCredentialsException;
import com.alephzero.alephzero.core.services.user.UserRoleService;
import com.alephzero.alephzero.core.services.user.UserService;
import com.alephzero.alephzero.core.models.User;
import com.alephzero.alephzero.core.models.UserRole;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service for handling user authentication-related operations.
 *
 * <p>Provides registration, login, refresh and logout. Access tokens are signed
 * JWTs (short-lived) and refresh tokens are opaque values rotated on every use.
 * The system is fully stateless: no HTTP session is created.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final RefreshTokenService refreshTokenService;

    /**
     * Registers a new user with the provided registration details.
     *
     * @param request the registration details (username, email, password, confirmation)
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

    /**
     * Authenticates a user and issues a fresh token pair.
     *
     * @param request the login credentials (username or email + password)
     * @return the new access token, refresh token and access token lifetime
     * @throws InvalidCredentialsException if the user does not exist or the password does not match
     */
    @Transactional
    public AuthResult login(LoginRequest request) {
        User user = findUserOrFail(request.username());

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = refreshTokenService.issue(user);

        return new AuthResult(accessToken, refreshToken, jwtService.getAccessTokenTtl().toSeconds());
    }

    /**
     * Rotates a refresh token and issues a new access token.
     *
     * @param refreshToken the raw refresh token from the client cookie
     * @return the new access token, rotated refresh token and access token lifetime
     */
    @Transactional
    public AuthResult refresh(String refreshToken) {
        RefreshTokenService.Rotation rotation = refreshTokenService.rotate(refreshToken);
        String accessToken = jwtService.generateAccessToken(rotation.user());

        return new AuthResult(accessToken, rotation.refreshToken(), jwtService.getAccessTokenTtl().toSeconds());
    }

    /**
     * Revokes the given refresh token (logout).
     *
     * @param refreshToken the raw refresh token from the client cookie
     */
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenService.revoke(refreshToken);
    }

    /**
     * Loads a user by username or email, translating the "not found" case into an
     * {@link InvalidCredentialsException} so that authentication failures do not
     * leak whether an account exists.
     */
    private User findUserOrFail(String identifier) {
        try {
            return userService.findUserByIdentifierReadOnly(identifier);
        } catch (EntityNotFoundException e) {
            throw new InvalidCredentialsException();
        }
    }

    /**
     * Result of a successful authentication/refresh.
     *
     * @param accessToken      the signed JWT
     * @param refreshToken     the raw refresh token (to be set in an HttpOnly cookie)
     * @param expiresInSeconds access token lifetime in seconds
     */
    public record AuthResult(String accessToken, String refreshToken, long expiresInSeconds) {
    }
}
