package com.alephzero.alephzero.core.services.auth;

import com.alephzero.alephzero.core.exception.InvalidRefreshTokenException;
import com.alephzero.alephzero.core.models.RefreshToken;
import com.alephzero.alephzero.core.models.User;
import com.alephzero.alephzero.core.repositories.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Manages the lifecycle of refresh tokens: issuing, rotating and revoking them.
 * <p>
 * Only the SHA-256 hash of a token is persisted (never the raw value). Rotation
 * replaces the stored hash on every refresh, so a leaked token becomes useless as
 * soon as the legitimate client refreshes.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTService jwtService;

    /**
     * Issues a new refresh token for the user, replacing any previous one.
     *
     * @param user the user to issue the token for
     * @return the raw refresh token value (to be sent in the HttpOnly cookie)
     */
    @Transactional
    public String issue(User user) {
        String rawToken = jwtService.generateRefreshTokenValue();

        RefreshToken token = refreshTokenRepository.findByUserId(user.getId())
                .orElseGet(RefreshToken::new);
        token.setUser(user);
        token.setTokenHash(jwtService.hashRefreshToken(rawToken));
        token.setExpiresAt(LocalDateTime.now().plus(jwtService.getRefreshTokenTtl()));
        refreshTokenRepository.save(token);

        return rawToken;
    }

    /**
     * Validates a raw refresh token and rotates it (a new value is stored).
     *
     * @param rawToken the raw token received from the client cookie
     * @return the owning user together with the newly generated raw token
     * @throws InvalidRefreshTokenException if the token is missing, unknown or expired
     */
    @Transactional
    public Rotation rotate(String rawToken) {
        String hash = jwtService.hashRefreshToken(rawToken);
        RefreshToken token = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(InvalidRefreshTokenException::new);

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new InvalidRefreshTokenException();
        }

        String newRawToken = jwtService.generateRefreshTokenValue();
        token.setTokenHash(jwtService.hashRefreshToken(newRawToken));
        token.setExpiresAt(LocalDateTime.now().plus(jwtService.getRefreshTokenTtl()));
        refreshTokenRepository.save(token);

        return new Rotation(token.getUser(), newRawToken);
    }

    /**
     * Revokes the refresh token matching the given raw value (best effort).
     *
     * @param rawToken the raw token received from the client cookie
     */
    @Transactional
    public void revoke(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return;
        }
        refreshTokenRepository.findByTokenHash(jwtService.hashRefreshToken(rawToken))
                .ifPresent(refreshTokenRepository::delete);
    }

    /** Revokes every refresh token of the given user. */
    @Transactional
    public void revokeAllForUser(User user) {
        refreshTokenRepository.deleteByUserId(user.getId());
    }

    /** Result of a rotation: the owner of the token and its new raw value. */
    public record Rotation(User user, String refreshToken) {
    }
}
