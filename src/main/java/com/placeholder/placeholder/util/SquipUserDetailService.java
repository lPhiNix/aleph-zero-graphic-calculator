package com.placeholder.placeholder.util;

import com.placeholder.placeholder.api.user.service.UserService;
import com.placeholder.placeholder.db.models.UserRole;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * SquipUserDetailService is a custom implementation of UserDetailsService
 * that retrieves user details from the database based on the username.
 */
@Service
@RequiredArgsConstructor
public class SquipUserDetailService implements UserDetailsService {
    private final UserService userService;

    /**
     * Loads user details by identifier (username Or email).
     *
     * @param username the identifier of the user to load
     * @return UserDetails containing user information
     * @throws EntityNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws EntityNotFoundException {
        com.placeholder.placeholder.db.models.User user = userService.findUserByIdentifier(username);
        UserRole role = user.getRole();

        return User.withUsername(user.getUsername())
                .roles(role.getName())
                .password(user.getPassword()) // is encoded.
                .build();
    }

}
