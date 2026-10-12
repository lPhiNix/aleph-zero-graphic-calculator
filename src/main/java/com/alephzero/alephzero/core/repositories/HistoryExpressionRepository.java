package com.alephzero.alephzero.core.repositories;

import com.alephzero.alephzero.core.models.HistoryExpression;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryExpressionRepository extends JpaRepository<HistoryExpression, Integer> {
}