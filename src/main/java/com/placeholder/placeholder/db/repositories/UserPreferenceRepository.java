package com.placeholder.placeholder.db.repositories;

import com.placeholder.placeholder.db.models.entities.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Integer> {
    Optional<UserPreference> findByUser_Username(String userUsername);
}