package com.placeholder.placeholder.api.user.service;

import com.placeholder.placeholder.db.models.UserRole;
import com.placeholder.placeholder.db.repositories.UserRoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRoleService {
    private static final String DEFAULT_ROLE = "USER";
    private final UserRoleRepository repository;

    public UserRole findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User role with id '%d' not found", id)));
    }

    public List<UserRole> findAll() {
        return repository.findAll();
    }

    public UserRole findByName(String name) {
        return repository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User role with name '%s' not found", name)));
    }

    public UserRole getDefaultRole() {
        return findByName(DEFAULT_ROLE);
    }

    public UserRole getAdminRole() {
        return findByName("ADMIN");
    }
}
