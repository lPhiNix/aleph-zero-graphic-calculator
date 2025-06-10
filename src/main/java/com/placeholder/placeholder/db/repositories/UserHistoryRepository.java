package com.placeholder.placeholder.db.repositories;

import com.placeholder.placeholder.db.models.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserHistoryRepository extends JpaRepository<UserHistory, Integer> {
}