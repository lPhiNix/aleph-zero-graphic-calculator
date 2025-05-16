package com.placeholder.placeholder.api.util.common.auth;

import com.placeholder.placeholder.api.user.service.UserService;
import com.placeholder.placeholder.db.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Custom implementation of {@link org.springframework.security.core.userdetails.UserDetailsService}
 * that retrieves user information.
 *
 * <p>
 * This service is used by Spring Security during the authentication process to load
 * user-specific data based on the provided username (in this case, the user's email).
 * </p>
 *
 * <p>
 * The returned {@link UserDetails} object includes the user's email, password, and granted authorities (roles).
 * </p>
 */
@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final UserService userService;

    /**
     * Constructs a new instance of {@code UserDetailsService} with the given {@link UserService}.
     *
     * @param userService the service used to retrieve users
     */
    public UserDetailsService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Loads the user from the database by their email.
     *
     * @param username the email identifying the user whose data is required
     * @return a fully populated {@link UserDetails} object
     * @throws UsernameNotFoundException if the user could not be found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findUserByEmail(username);

        return org.springframework.security.core.userdetails.User.builder()
                .username(username)
                .password(user.getPassword())
                .authorities(getParsedAuthorities(user))
                .build();
    }

    /**
     * Parses the user's role into a format that Spring Security can understand.
     * The role is prefixed with "ROLE_" as required by Spring conventions.
     *
     * @param user the user entity
     * @return a list of granted authorities containing the user's role
     */
    private List<GrantedAuthority> getParsedAuthorities(User user) {
        String role = String.format("ROLE_%s", user.getRole().getName().toUpperCase());
        return List.of(new SimpleGrantedAuthority(role));
    }
}

