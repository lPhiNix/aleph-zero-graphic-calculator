package com.placeholder.placeholder.api.auth.components;

import com.placeholder.placeholder.api.auth.service.SquipUserDetailService;
import com.placeholder.placeholder.api.math.service.persistence.MathUserHistoryService;
import com.placeholder.placeholder.db.models.User;
import com.placeholder.placeholder.db.models.UserHistory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("userHistorySecurity")
public class UserHistorySecurity {

    private final MathUserHistoryService mathUserHistoryService;
    private final SquipUserDetailService squipUserDetailService;

    public UserHistorySecurity(MathUserHistoryService mathUserHistoryService, SquipUserDetailService squipUserDetailService) {
        this.mathUserHistoryService = mathUserHistoryService;
        this.squipUserDetailService = squipUserDetailService;
    }

    public boolean hasAccessTo(Integer id, Authentication authentication) {
        UserHistory history = mathUserHistoryService.findByIdReadOnly(id);
        User currentUser = squipUserDetailService.getCurrentUser();
        return currentUser.equals(history.getUser());
    }
}
