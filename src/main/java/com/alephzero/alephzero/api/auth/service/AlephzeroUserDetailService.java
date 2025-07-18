package com.alephzero.alephzero.api.auth.service;

import com.alephzero.alephzero.api.user.service.UserService;
import com.alephzero.alephzero.db.models.UserRole;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

/**
 * SquipUserDetailService is a custom implementation of UserDetailsService
 * that retrieves user details from the database based on the username.
 */
@Service
@RequiredArgsConstructor
public class AlephzeroUserDetailService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(AlephzeroUserDetailService.class);

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
        com.alephzero.alephzero.db.models.User user = userService.findUserByIdentifierReadOnly(username);
        UserRole role = user.getRole();

        return User.withUsername(user.getUsername())
                .roles(role.getName())
                .password(user.getPassword()) // is encoded.
                .build();
    }


    /**
     * Retrieves the authenticated user based on the JWT token stored in the security context.
     *
     * <p>This method performs the following steps:
     * <ul>
     *   <li>Extracts the authentication object from the {@link SecurityContextHolder}.</li>
     *   <li>Verifies that the authentication is based on a valid {@link Jwt} token.</li>
     *   <li>Extracts the username from the <code>preferred_username</code> claim.</li>
     *   <li>Loads the user from the database using the extracted username.</li>
     * </ul>
     *
     * @return the authenticated {@link com.alephzero.alephzero.db.models.User} from the database
     * @throws IllegalStateException if the authentication is missing, invalid, or the user is not found
     */
    public com.alephzero.alephzero.db.models.User getCurrentUser() {
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

        var user = userService.findUserByIdentifierReadOnly(username);
        if (user == null) {
            logger.error("No user found with identifier: {}", username);
            throw new IllegalStateException("Authenticated user not found in database");
        }

        logger.info("Authenticated user loaded: {}", user);
        return user;
    }
}
