package com.placeholder.placeholder.api.util.common.messages;

import com.placeholder.placeholder.api.util.common.messages.dto.error.ErrorCategory;
import com.placeholder.placeholder.api.util.common.messages.dto.error.details.ValidationErrorDetail;
import jakarta.validation.ConstraintViolation;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import jakarta.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for processing and extracting error details from common error scenarios
 * encountered in Spring applications, particularly within controllers and exception handlers.
 */
public class ApiResponseUtils {

    /**
     * Extracts a list of {@link ValidationErrorDetail} from a {@link BindingResult}, which typically
     * contains validation errors resulting from Spring's data binding and validation mechanisms.
     * This method processes both field-specific errors and global errors.
     *
     * @param bindingResult The {@code BindingResult} object containing validation errors.
     * @param category      The {@link ErrorCategory} to associate with these validation errors.
     * This helps in classifying the type of error.
     * @return A sorted {@code List} of {@link ValidationErrorDetail} instances, each representing a specific
     * validation error with its category, field (if applicable), message, and rejected value.
     */
    public static List<ValidationErrorDetail> getErrorDetails(BindingResult bindingResult, ErrorCategory category) {
        List<ValidationErrorDetail> details = new ArrayList<>();

        // Process field-specific errors
        details.addAll(
                bindingResult.getFieldErrors().stream()
                        .map(fieldError -> new ValidationErrorDetail(
                                category,
                                fieldError.getField(),
                                fieldError.getDefaultMessage(),
                                fieldError.getRejectedValue()
                        ))
                        .toList()
        );

        // Process global errors (non-field specific)
        details.addAll(
                bindingResult.getGlobalErrors().stream()
                        .map(error -> new ValidationErrorDetail(
                                ErrorCategory.UNKNOWN, // Typically global errors don't map to a specific category
                                "global",
                                error.getDefaultMessage(),
                                null // Global errors usually don't have a specific rejected value
                        ))
                        .sorted(Comparator.comparing(ValidationErrorDetail::category))
                        .toList()
        );

        return details;
    }


    /**
     * Extracts a list of {@link ValidationErrorDetail} from a {@code Set} of Jakarta Bean Validation
     * {@link ConstraintViolation} instances. This method is useful for handling errors arising from
     * programmatic or annotation-based constraint validation.
     *
     * @param violations The {@code Set} of {@code ConstraintViolation} objects representing validation failures.
     * @param category   The {@link ErrorCategory} to associate with these constraint violations.
     * This helps in classifying the type of error.
     * @return A {@code List} of {@link ValidationErrorDetail} instances, each representing a constraint
     * violation with its category, property path, message, and the invalid value. The list is
     * sorted by the error category for consistent ordering.
     */
    public static List<ValidationErrorDetail> getErrorDetails(Set<ConstraintViolation<?>> violations, ErrorCategory category) {
        return violations.stream()
                .map(violation -> new ValidationErrorDetail(
                        category,
                        violation.getPropertyPath().toString(), // Path to the invalid property
                        violation.getMessage(),                 // Validation error message
                        violation.getInvalidValue()            // The value that failed validation
                ))
                .sorted(Comparator.comparing(ValidationErrorDetail::category)) // Ensure consistent ordering by category
                .collect(Collectors.toList());
    }
}