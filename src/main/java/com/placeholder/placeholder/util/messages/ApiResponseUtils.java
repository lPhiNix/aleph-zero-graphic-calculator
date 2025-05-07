package com.placeholder.placeholder.util.messages;

import com.placeholder.placeholder.util.messages.dto.error.ErrorCategory;
import com.placeholder.placeholder.util.messages.dto.error.details.ValidationErrorDetail;
import jakarta.validation.ConstraintViolation;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for getting and parsing error responses from common errors.
 */
public class ApiResponseUtils {

    /**
     * Extracts {@link ValidationErrorDetail} list from a {@link BindingResult} containing field validation errors.
     *
     * @param bindingResult the binding result containing validation errors
     * @return a list of {@link ValidationErrorDetail} instances representing each validation error
     */
    public static List<ValidationErrorDetail> getErrorDetails(BindingResult bindingResult, ErrorCategory category) {
        List<ValidationErrorDetail> details = new ArrayList<>();

        // Field-specific errors
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

        details.addAll(
                bindingResult.getGlobalErrors().stream()
                        .map(error -> new ValidationErrorDetail(
                                ErrorCategory.UNKNOWN,
                                "global",
                                error.getDefaultMessage(),
                                null
                        ))
                        .sorted(Comparator.comparing(ValidationErrorDetail::category))
                        .toList()
        );

        return details;
    }


    /**
     * Extracts {@link ValidationErrorDetail} list from a set of {@link ConstraintViolation} instances.
     *
     * @param violations the set of constraint violations
     * @return a list of {@link ValidationErrorDetail} instances representing each constraint violation
     */
    public static List<ValidationErrorDetail> getErrorDetails(Set<ConstraintViolation<?>> violations, ErrorCategory category) {
        return violations.stream()
                .map(violation -> new ValidationErrorDetail(
                        category,
                        violation.getPropertyPath().toString(),
                        violation.getMessage(),
                        violation.getInvalidValue()
                ))
                .sorted(Comparator.comparing(ValidationErrorDetail::category))
                .collect(Collectors.toList());
    }
}