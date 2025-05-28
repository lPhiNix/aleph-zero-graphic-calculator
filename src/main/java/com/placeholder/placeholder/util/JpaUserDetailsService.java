package com.placeholder.placeholder.util;

import com.placeholder.placeholder.api.user.service.UserService;
import com.placeholder.placeholder.db.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {
    private final UserService userService;

    /**
     * Loads user details by username.
     *
     * @param username the identifier of the user to load
     * @return UserDetails containing user information
     */
    @Override
    public UserDetails loadUserByUsername(String username)  {
        User user = userService.findUserByIdentifier(username);

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().toString()) // ROlE_%name%
                .build();

    }
}
