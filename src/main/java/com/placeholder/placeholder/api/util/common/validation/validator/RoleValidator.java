package com.placeholder.placeholder.api.util.common.validation.validator;

import com.placeholder.placeholder.db.models.UserRole;
import com.placeholder.placeholder.db.repositories.UserRoleRepository;
import com.placeholder.placeholder.api.util.common.validation.conditions.ValidRole;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class RoleValidator implements ConstraintValidator<ValidRole, String> {
    private final UserRoleRepository userRoleRepository;

    @Autowired
    public RoleValidator(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        List<String> roles = userRoleRepository.findAll().stream().map(
                UserRole::getName).toList();

        return roles.contains(value);
    }
}
