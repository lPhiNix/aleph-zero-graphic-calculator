package com.alephzero.alephzero.core.repositories;

import com.alephzero.alephzero.core.models.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for {@link RefreshToken} entities.
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

    /** Finds the refresh token whose stored hash matches the given hash. */
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    /** Finds the (single) active refresh token of the given user. */
    Optional<RefreshToken> findByUserId(Integer userId);

    /** Removes the active refresh token of the given user, if any. */
    void deleteByUserId(Integer userId);
}
