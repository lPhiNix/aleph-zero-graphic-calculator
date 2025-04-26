package com.placeholder.placeholder.db;

import com.placeholder.placeholder.db.models.dto.UserCreationRequest;
import com.placeholder.placeholder.db.models.entities.User;
import com.placeholder.placeholder.db.models.entities.UserRole;
import com.placeholder.placeholder.db.repositories.UserRepository;
import com.placeholder.placeholder.db.repositories.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public User createUser(UserCreationRequest userCreationRequest) {
        User user = new User();
        user.setEmail(userCreationRequest.email());
        user.setUsername(userCreationRequest.username());
        user.setPassword(passwordEncoder.encode(userCreationRequest.password())); //Encodes the password


        UserRole role = userCreationRequest.usesNumericIdentifier() ?
                userRoleRepository.getReferenceById(userCreationRequest.roleId()) :
                userRoleRepository.getUserRoleByName(userCreationRequest.roleName());

        user.setRole(role);

        return userRepository.save(user);
    }
}
