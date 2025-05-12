package com.placeholder.placeholder.db.repositories;

import com.placeholder.placeholder.db.models.MathExpression;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MathExpressionRepository extends JpaRepository<MathExpression, Integer> {
}