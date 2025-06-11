package com.alephzero.alephzero.db.repositories;

import com.alephzero.alephzero.db.models.MathExpression;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MathExpressionRepository extends JpaRepository<MathExpression, Integer> {
}