package com.alephzero.alephzero.db.repositories;

import com.alephzero.alephzero.db.models.ExpressionPreference;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpressionPreferenceRepository extends JpaRepository<ExpressionPreference, Integer> {
}