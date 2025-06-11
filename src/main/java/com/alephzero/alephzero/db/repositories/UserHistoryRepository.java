package com.alephzero.alephzero.db.repositories;

import com.alephzero.alephzero.db.models.User;
import com.alephzero.alephzero.db.models.UserHistory;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserHistoryRepository extends JpaRepository<UserHistory, Integer> {
    List<UserHistory> findAllByUser(@NotNull User user);
}