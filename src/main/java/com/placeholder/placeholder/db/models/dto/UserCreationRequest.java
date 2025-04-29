package com.placeholder.placeholder.db.models.dto;

import com.placeholder.placeholder.util.validation.conditions.ValidPassword;
import com.placeholder.placeholder.util.validation.conditions.ValidRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

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