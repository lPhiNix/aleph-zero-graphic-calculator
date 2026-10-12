package com.alephzero.alephzero.core.services.auth.components;

import com.alephzero.alephzero.core.services.auth.AlephzeroUserDetailService;
import com.alephzero.alephzero.core.services.math.persistence.MathUserHistoryService;
import com.alephzero.alephzero.core.models.User;
import com.alephzero.alephzero.core.models.UserHistory;
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
