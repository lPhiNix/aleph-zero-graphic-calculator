package com.placeholder.placeholder.api.auth.service;

import com.placeholder.placeholder.api.user.service.UserService;
import com.placeholder.placeholder.db.models.UserRole;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

/**
 * SquipUserDetailService is a custom implementation of UserDetailsService
 * that retrieves user details from the database based on the username.
 */
@Service
@RequiredArgsConstructor
public class SquipUserDetailService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(SquipUserDetailService.class);

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


    public com.placeholder.placeholder.db.models.User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            logger.error("No valid JWT authentication found");
            throw new IllegalStateException("No authenticated user found");
        }

        String username = jwt.getClaimAsString("preferred_username");

        if (username == null) {
            logger.error("Claim 'preferred_username' not found in token");
            throw new IllegalStateException("Username not present in token");
        }

        logger.info("Extracted username from token: {}", username);

        var user = userService.findUserByIdentifier(username);
        if (user == null) {
            logger.error("No user found with identifier: {}", username);
            throw new IllegalStateException("Authenticated user not found in database");
        }

        logger.info("Authenticated user loaded: {}", user);
        return user;
    }

}
