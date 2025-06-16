package com.alephzero.alephzero.api.user.service;

import com.alephzero.alephzero.api.util.common.service.AbstractCrudService;
import com.alephzero.alephzero.db.models.UserRole;
import com.alephzero.alephzero.db.repositories.UserRoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service layer for handling operations related to {@link UserRole}.
 * <p>
 * Provides methods to retrieve roles by ID, name, or retrieve default/admin roles.
 */
@Service
public class UserRoleService extends AbstractCrudService<UserRole, Integer, UserRoleRepository> {

    /**
     * The default role assigned to new users.
     */
    private static final String DEFAULT_ROLE = "USER";

    public UserRoleService(UserRoleRepository repository) {
        super(repository);
    }

    /**
     * Finds a user role by its name.
     *
     * @param name the name of the role
     * @return the found {@link UserRole}
     * @throws EntityNotFoundException if no role with the given name exists
     */
    public UserRole findByName(String name) {
        return repository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User role with name '%s' not found", name)));
    }

    /**
     * Returns the default user role.
     *
     * @return the default {@link UserRole}
     */
    public UserRole getDefaultRole() {
        return findByName(DEFAULT_ROLE);
    }

    /**
     * Returns the admin user role.
     *
     * @return the admin {@link UserRole}
     */
    public UserRole getAdminRole() {
        return findByName("ADMIN");
    }
}
