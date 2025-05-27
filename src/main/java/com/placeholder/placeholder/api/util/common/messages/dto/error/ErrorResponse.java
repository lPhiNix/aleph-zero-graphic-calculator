package com.placeholder.placeholder.api.util.common.messages.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.placeholder.placeholder.api.util.common.messages.dto.content.MessageContent;
import com.placeholder.placeholder.api.util.common.messages.dto.error.details.ApiErrorDetail;
import com.placeholder.placeholder.api.util.common.messages.dto.error.details.ValidationErrorDetail;

import java.util.List;

/**
 * Standard DTO response message for errors
 * @param title a detailed description of the error.
 * @param summary brief summary of the error, optional parameter.
 * @param errors optional value, contains a list of {@link ValidationErrorDetail} for more information
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String title,
        String summary,
        List<ApiErrorDetail> errors
) implements MessageContent
{ }