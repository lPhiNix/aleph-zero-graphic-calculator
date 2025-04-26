package com.placeholder.placeholder.services;

import com.placeholder.placeholder.db.models.entities.User;
import com.placeholder.placeholder.db.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    private final UserRepository userRepository;

    @Autowired
    public UsuarioService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
