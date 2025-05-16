package com.placeholder.placeholder.api.util.common.validation.validator;

import com.placeholder.placeholder.db.models.UserRole;
import com.placeholder.placeholder.db.repositories.UserRoleRepository;
import com.placeholder.placeholder.api.util.common.validation.annotations.ValidRole;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Custom {@link jakarta.validation.ConstraintValidator} to validate user roles.
 * This validator checks if a given role value exists in the database using the {@link UserRole} repository.
 */
public class RoleValidator implements ConstraintValidator<ValidRole, String> {

    private final UserRoleRepository userRoleRepository;

    /**
     * Constructs a new {@code RoleValidator} with the provided {@link UserRoleRepository}.
     *
     * @param userRoleRepository The repository used to access and check existing user roles.
     */
    @Autowired
    public RoleValidator(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    /**
     * Checks if the given role value is valid by verifying its existence in the database.
     *
     * @param value   The role value to validate.
     * @param context Context in which the constraint is evaluated.
     * @return {@code true} if the role value exists, {@code false} otherwise.
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        // Null values are generally validated using @NotNull, this check is to make sure the value is not null.
        if (value == null) {
            return false;
        }

        // Retrieve all role names from the UserRole repository.
        List<String> roles = userRoleRepository.findAll().stream()
                .map(UserRole::getName)
                .toList();

        // Check if the provided value exists in the list of available roles.
        return roles.contains(value);
    }
}
