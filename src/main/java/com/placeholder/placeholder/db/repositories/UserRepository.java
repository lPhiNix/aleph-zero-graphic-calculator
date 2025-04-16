package com.placeholder.placeholder.db.repositories;

import com.placeholder.placeholder.db.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findUserByEmail(String email);
    List<User> findUsersByRole_Name(String roleName);
    List<User> findUserByUsername(String username);
}