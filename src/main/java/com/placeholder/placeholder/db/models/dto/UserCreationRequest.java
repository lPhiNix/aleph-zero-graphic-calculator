package com.placeholder.placeholder.db.models.dto;

import com.placeholder.placeholder.util.validation.conditions.OnlyOneField;
import com.placeholder.placeholder.util.validation.conditions.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

@OnlyOneField(fields = {"roleId", "roleName"})
public record UserCreationRequest(
        @NotBlank
        String username,

        @NotBlank
        @Email
        String email,

        @NotBlank
        @ValidPassword
        String password,

        //Only one of these must be provided.
        @PositiveOrZero
        int roleId,
        String roleName
) {
        public boolean usesNumericIdentifier(){
            return roleName == null;
        }
}