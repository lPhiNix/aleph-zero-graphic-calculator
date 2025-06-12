package com.alephzero.alephzero.api.auth.components;

import com.alephzero.alephzero.api.auth.service.AlephzeroUserDetailService;
import com.alephzero.alephzero.api.math.service.persistence.MathUserHistoryService;
import com.alephzero.alephzero.db.models.User;
import com.alephzero.alephzero.db.models.UserHistory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("userHistorySecurity")
public class UserHistorySecurity {

    private final MathUserHistoryService mathUserHistoryService;
    private final AlephzeroUserDetailService alephzeroUserDetailService;

    public UserHistorySecurity(MathUserHistoryService mathUserHistoryService, AlephzeroUserDetailService alephzeroUserDetailService) {
        this.mathUserHistoryService = mathUserHistoryService;
        this.alephzeroUserDetailService = alephzeroUserDetailService;
    }

    public boolean hasAccessTo(Integer id, Authentication authentication) {
        UserHistory history = mathUserHistoryService.findByIdReadOnly(id);
        User currentUser = alephzeroUserDetailService.getCurrentUser();
        return currentUser.equals(history.getUser());
    }
}
