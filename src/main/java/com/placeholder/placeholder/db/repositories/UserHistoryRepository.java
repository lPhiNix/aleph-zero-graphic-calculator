package com.placeholder.placeholder.db.repositories;

import com.placeholder.placeholder.db.models.User;
import com.placeholder.placeholder.db.models.UserHistory;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserHistoryRepository extends JpaRepository<UserHistory, Integer> {
    List<UserHistory> findAllByUser(@NotNull User user);
}