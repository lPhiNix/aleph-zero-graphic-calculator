package com.alephzero.alephzero.core.services.auth;

import com.alephzero.alephzero.core.services.user.UserService;
import com.alephzero.alephzero.core.models.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Resolves the currently authenticated user from the security context.
 * <p>
 * The context is populated by {@link com.alephzero.alephzero.infrastructure.security.JwtAuthenticationFilter}
 * with the username extracted from the access token.
 */
@Service
@RequiredArgsConstructor
public class AlephzeroUserDetailService {

    private static final Logger logger = LoggerFactory.getLogger(AlephzeroUserDetailService.class);

    private final UserService userService;

    /**
     * Returns the authenticated user backed by the current access token.
     *
     * @return the {@link User} from the database
     * @throws IllegalStateException if there is no authenticated user in the context
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            logger.error("No authenticated user found in the security context");
            throw new IllegalStateException("No authenticated user found");
        }

        String username = authentication.getName();
        User user = userService.findUserByIdentifierReadOnly(username);
        if (user == null) {
            logger.error("No user found with identifier: {}", username);
            throw new IllegalStateException("Authenticated user not found in database");
        }

        return user;
    }
}
