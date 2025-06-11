package com.alephzero.alephzero.api.user.dto;

import com.alephzero.alephzero.api.user.validation.annotations.ValidPassword;
import com.alephzero.alephzero.api.user.validation.annotations.ValidRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserCreationRequest(
        @NotBlank
        String username,

        @NotBlank
        @Email
        String email,

        @NotBlank
        @ValidPassword
        String password,

        @ValidRole
        String roleName
) {
        public boolean usesNumericIdentifier(){
            return roleName == null;
        }
}