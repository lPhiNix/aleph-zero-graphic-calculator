package com.alephzero.alephzero.db.repositories;

import com.alephzero.alephzero.db.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findUserByEmail(String email);
    Optional<User> findUsersByRole_Name(String roleName);
    Optional<User> findUserByUsername(String username);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // This method allows searching for a user by either username or email.
    @Query("SELECT u FROM User u WHERE u.username = :input OR u.email = :input")
    Optional<User> findByUsernameOrEmail(@Param("input") String identifier);
}