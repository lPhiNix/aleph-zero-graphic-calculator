package com.alephzero.alephzero.api.user.service;

import com.alephzero.alephzero.api.util.common.service.AbstractCrudService;
import com.alephzero.alephzero.db.models.User;
import com.alephzero.alephzero.db.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * General service class for performing operations on {@link User} entities.
 */
@Service
public class UserService extends AbstractCrudService<User, Integer, UserRepository> {

    /**
     * Constructs a new {@code UserService} with the given repository.
     *
     * @param repository the {@link UserRepository} to be used for database access
     */
    public UserService(UserRepository repository) {
        super(repository);
    }

    /**
     * Retrieves a user by their username or email in read-only mode.
     *
     * @param identifier the username or email of the user
     * @return the found {@link User}
     * @throws EntityNotFoundException if no user is found with the given identifier
     */
    @Transactional(readOnly = true)
    public User findUserByIdentifierReadOnly(String identifier) {
        return repository.findByUsernameOrEmail(identifier)
                .orElseThrow(() -> new EntityNotFoundException(identifier));
    }

    /**
     * Retrieves a user by their unique ID.
     *
     * @param id the ID of the user
     * @return the found {@link User}
     * @throws EntityNotFoundException if no user is found with the given ID
     */
    public User findUserById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
    }

    /**
     * Saves the provided user to the database.
     *
     * @param user the {@link User} to save
     * @return the saved {@link User}
     */
    public User save(User user) {
        repository.save(user);
        return user;
    }

    /**
     * Checks whether a user with the given username already exists.
     *
     * @param username the username to check
     * @return {@code true} if a user with that username exists, {@code false} otherwise
     */
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    /**
     * Checks whether a user with the given email already exists.
     *
     * @param email the email to check
     * @return {@code true} if a user with that email exists, {@code false} otherwise
     */
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }
}
