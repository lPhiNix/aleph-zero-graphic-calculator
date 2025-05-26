package com.placeholder.placeholder.api.user.service;

import com.placeholder.placeholder.db.mappers.UserMapper;
import com.placeholder.placeholder.db.models.User;
import com.placeholder.placeholder.db.repositories.UserPreferenceRepository;
import com.placeholder.placeholder.db.repositories.UserRepository;
import com.placeholder.placeholder.db.repositories.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserRoleRepository roleRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public User findUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(String.valueOf(id)));
    }

    public User save(User user) {
        userRepository.save(user);
        return user;
    }
}
