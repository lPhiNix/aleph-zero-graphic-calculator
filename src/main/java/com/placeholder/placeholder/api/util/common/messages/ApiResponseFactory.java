package com.placeholder.placeholder.api.util.common.messages;

import com.placeholder.placeholder.util.enums.AppCode;
import com.placeholder.placeholder.api.util.common.messages.dto.ApiResponse;
import com.placeholder.placeholder.api.util.common.messages.dto.content.MessageContent;
import com.placeholder.placeholder.api.util.common.messages.dto.error.details.ApiErrorDetail;
import com.placeholder.placeholder.api.util.common.messages.dto.error.details.ValidationErrorDetail;
import com.placeholder.placeholder.api.util.common.messages.dto.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ApiResponseFactory {

    private final ObjectFactory<HttpServletRequest> requestFactory;

    @Autowired
    public ApiResponseFactory(ObjectFactory<HttpServletRequest> requestFactory) {
        this.requestFactory = requestFactory;
    }

    private <T extends MessageContent> ResponseEntity<ApiResponse<T>> build(AppCode code, String headerMessage, T content) {
        ApiResponse<T> response = new ApiResponse<>(
                code,
                headerMessage,
                requestFactory.getObject().getRequestURI(),
                content
        );

        return ResponseEntity.status(code.getStatus()).body(response);
    }

    private ErrorResponse buildError(String title, String summary, List<? extends ApiErrorDetail> details) {
        List<ApiErrorDetail> errors = details != null
                ? List.copyOf(details)
                : Collections.emptyList();

        return new ErrorResponse(title, summary, errors);
    }

    private String getErrorSummary(List<ValidationErrorDetail> details) {
        String categories = details.stream()
                .map(ValidationErrorDetail::category)
                .filter(Objects::nonNull)
                .map(Enum::name)
                .distinct()
                .collect(Collectors.joining(", ", "{", "}"));

        return String.format("%d errors found when processing request with categories: %s", details.size(), categories);
    }

    public <T extends MessageContent> ResponseEntity<ApiResponse<T>> ok(T content) {
        return build(AppCode.OK, AppCode.OK.getSimpleMessage(), content);
    }

    // == ERRORS: GENERIC ==
    public ResponseEntity<ApiResponse<ErrorResponse>> error(AppCode code, String title, String summary, List<ApiErrorDetail> details) {
        ErrorResponse error = buildError(title, summary, details);
        return build(code, code.getSimpleMessage(), error);
    }

    public ResponseEntity<ApiResponse<ErrorResponse>> error(AppCode code, String title, List<ApiErrorDetail> details) {
        ErrorResponse error = buildError(title, null, details);
        return build(code, code.getSimpleMessage(), error);
    }

    public ResponseEntity<ApiResponse<ErrorResponse>> error(AppCode code, String title, String summary) {
        ErrorResponse error = buildError(title, summary, null);
        return build(code, code.getSimpleMessage(), error);
    }

    // == ERRORS: VALIDATION ==z
    public ResponseEntity<ApiResponse<ErrorResponse>> validationError(String title, List<ValidationErrorDetail> details) {
        AppCode code = AppCode.VALIDATION_ERROR;
        String summary = getErrorSummary(details);

        ErrorResponse error = buildError(title, summary, details);
        return build(code, code.getSimpleMessage(), error);
    }
}