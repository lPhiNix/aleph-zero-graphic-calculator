package com.placeholder.placeholder.db.repositories;

import com.placeholder.placeholder.db.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findUserByEmail(String email);
    Optional<User> findUsersByRole_Name(String roleName);
    Optional<User> findUserByUsername(String username);
}