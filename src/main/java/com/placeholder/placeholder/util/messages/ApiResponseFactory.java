package com.placeholder.placeholder.util.messages;

import com.placeholder.placeholder.util.enums.AppCode;
import com.placeholder.placeholder.util.messages.builders.ApiResponseBuilder;
import com.placeholder.placeholder.util.messages.dto.ApiResponse;
import com.placeholder.placeholder.util.messages.dto.content.EmptyContentResponse;
import com.placeholder.placeholder.util.messages.dto.content.MessageContent;
import com.placeholder.placeholder.util.messages.dto.error.details.ApiErrorDetail;
import com.placeholder.placeholder.util.messages.dto.error.details.ValidationErrorDetail;
import com.placeholder.placeholder.util.messages.dto.error.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ApiResponseFactory {
    private static final String DEFAULT_OK_HEADER_MESSAGE = "Operation successful";

    private <T extends MessageContent> ResponseEntity<ApiResponse<T>> build(String path, AppCode code, String message, T content) {
        ApiResponse<T> response = ApiResponseBuilder.<T>builder()
                .status(code.getStatus().value())
                .code(code.value())
                .path(path)
                .message(message)
                .content(content)
                .build();

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

    // == STANDARD OK MESSAGE ==
    public ResponseEntity<ApiResponse<EmptyContentResponse>> ok(String path, String headerMessage) {
        return build(path, AppCode.OK, headerMessage, new EmptyContentResponse());
    }

    public ResponseEntity<ApiResponse<EmptyContentResponse>> ok(String path) {
        return build(path, AppCode.OK, DEFAULT_OK_HEADER_MESSAGE, new EmptyContentResponse());
    }

    // == ERRORS: GENERIC ==
    public ResponseEntity<ApiResponse<ErrorResponse>> error(String path, AppCode code, String title, List<ApiErrorDetail> details) {
        ErrorResponse error = buildError(title, null, details);
        return build(path, code, code.getSimpleMessage(), error);
    }

    public ResponseEntity<ApiResponse<ErrorResponse>> error(String path, AppCode code, String title, String summary) {
        ErrorResponse error = buildError(title, summary, null);
        return build(path, code, code.getSimpleMessage(), error);
    }

    public ResponseEntity<ApiResponse<ErrorResponse>> error(String path, AppCode code, String title, String summary, List<ApiErrorDetail> details) {
        ErrorResponse error = buildError(title, summary, details);
        return build(path, code, code.getSimpleMessage(), error);
    }

    // == ERRORS: VALIDATION ==z
    public ResponseEntity<ApiResponse<ErrorResponse>> validationError(String path, String title, List<ValidationErrorDetail> details) {
        AppCode code = AppCode.VALIDATION_ERROR;
        String summary = getErrorSummary(details);

        ErrorResponse error = buildError(title, summary, details);
        return build(path, code, code.getSimpleMessage(), error);
    }
}