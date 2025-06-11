package com.alephzero.alephzero.api.user.service;

import com.alephzero.alephzero.db.models.User;
import com.alephzero.alephzero.db.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(username));
    }

    @Transactional(readOnly = true)
    public User findUserByIdentifier(String identifier) {
        return userRepository.findByUsernameOrEmail(identifier)
                .orElseThrow(() -> new EntityNotFoundException(identifier));
    }

    public User findUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
    }

    public User save(User user) {
        userRepository.save(user);
        return user;
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
