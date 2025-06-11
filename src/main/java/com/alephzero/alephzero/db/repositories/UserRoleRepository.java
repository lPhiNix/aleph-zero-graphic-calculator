package com.alephzero.alephzero.db.repositories;

import com.alephzero.alephzero.db.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {

    UserRole getUserRoleByName(String name);

    boolean existsByName(String name);

    Optional<UserRole> findByName(String name);
}