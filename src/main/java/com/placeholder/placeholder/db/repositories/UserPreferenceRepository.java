package com.placeholder.placeholder.db.repositories;

import com.placeholder.placeholder.db.models.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Integer> {
}