package com.placeholder.placeholder.api.user.service;

import com.placeholder.placeholder.api.user.dto.UserCreationRequest;
import com.placeholder.placeholder.db.basicdto.UserDto;
import com.placeholder.placeholder.db.mappers.UserMapper;
import com.placeholder.placeholder.db.models.User;
import com.placeholder.placeholder.db.models.UserRole;
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
    private final UserRoleRepository userRoleRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
    }

    public UserDto createUser(UserCreationRequest userCreationRequest) {
        User user = new User();
        user.setEmail(userCreationRequest.email());
        user.setUsername(userCreationRequest.username());
        user.setPassword(passwordEncoder.encode(userCreationRequest.password())); //Encodes the password


        UserRole role =
                userRoleRepository.getUserRoleByName(userCreationRequest.roleName());

        user.setRole(role);
        userRepository.save(user);

        return userMapper.toDto(user);
    }
}
