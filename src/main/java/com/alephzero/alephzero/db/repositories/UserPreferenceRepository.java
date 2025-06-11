package com.alephzero.alephzero.db.repositories;

import com.alephzero.alephzero.db.models.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Integer> {
    Optional<UserPreference> findByUser_Username(String userUsername);
}