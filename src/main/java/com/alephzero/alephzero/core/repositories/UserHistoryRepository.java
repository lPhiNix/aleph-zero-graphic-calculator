package com.alephzero.alephzero.core.repositories;

import com.alephzero.alephzero.core.models.User;
import com.alephzero.alephzero.core.models.UserHistory;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserHistoryRepository extends JpaRepository<UserHistory, Integer> {
    List<UserHistory> findAllByUser(@NotNull User user);
}