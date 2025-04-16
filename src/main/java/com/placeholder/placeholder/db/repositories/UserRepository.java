package com.placeholder.placeholder.db.repositories;

import com.placeholder.placeholder.db.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}