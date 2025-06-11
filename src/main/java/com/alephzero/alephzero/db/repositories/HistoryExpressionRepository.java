package com.alephzero.alephzero.db.repositories;

import com.alephzero.alephzero.db.models.HistoryExpression;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryExpressionRepository extends JpaRepository<HistoryExpression, Integer> {
}