package com.placeholder.placeholder.api.user.dto;

import com.placeholder.placeholder.api.util.common.validation.annotations.ValidPassword;
import com.placeholder.placeholder.api.util.common.validation.annotations.ValidRole;
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