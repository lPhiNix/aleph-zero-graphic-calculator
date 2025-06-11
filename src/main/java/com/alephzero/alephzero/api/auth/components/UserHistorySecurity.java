package com.alephzero.alephzero.api.auth.components;

import com.alephzero.alephzero.api.auth.service.SquipUserDetailService;
import com.alephzero.alephzero.api.math.service.persistence.MathUserHistoryService;
import com.alephzero.alephzero.db.models.User;
import com.alephzero.alephzero.db.models.UserHistory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Security component used in {@code @PreAuthorize()} annotations to verify
 * if the currently authenticated user has access to a specific UserHistory resource.
 *
 * <p>This class helps enforce that users can only access their own history records.</p>
 *
 * <p>Usage example in a controller:
 * {@code @PreAuthorize("@userHistorySecurity.hasAccessTo(#id, authentication)")} </p>
 */
@Component("userHistorySecurity")
public class UserHistorySecurity {

    private final MathUserHistoryService mathUserHistoryService;
    private final SquipUserDetailService squipUserDetailService;

    /**
     * Constructs a new {@code UserHistorySecurity} with required services.
     *
     * @param mathUserHistoryService the service used to fetch UserHistory entities
     * @param squipUserDetailService the service used to obtain the current authenticated user
     */
    public UserHistorySecurity(MathUserHistoryService mathUserHistoryService, SquipUserDetailService squipUserDetailService) {
        this.mathUserHistoryService = mathUserHistoryService;
        this.squipUserDetailService = squipUserDetailService;
    }

    /**
     * Checks whether the currently authenticated user has access to the specified UserHistory resource.
     *
     * @param id the ID of the UserHistory entity
     * @param authentication the current authentication object (not used internally, but required by Spring Security)
     * @return {@code true} if the UserHistory belongs to the current user; {@code false} otherwise
     */
    public boolean hasAccessTo(Integer id, Authentication authentication) {
        UserHistory history = mathUserHistoryService.findByIdReadOnly(id);
        User currentUser = squipUserDetailService.getCurrentUser();
        return currentUser.equals(history.getUser());
    }
}
