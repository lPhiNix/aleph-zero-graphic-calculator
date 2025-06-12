package com.alephzero.alephzero.api.util.common.messages.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.alephzero.alephzero.api.util.common.messages.dto.error.details.ApiErrorDetail;
import com.alephzero.alephzero.api.util.common.messages.dto.error.details.ValidationErrorDetail;

import java.io.Serializable;
import java.util.List;

/**
 * Standard DTO response message for errors
 *
 * @param title   a detailed description of the error.
 * @param summary brief summary of the error, optional parameter.
 * @param errors  optional value, contains a list of {@link ValidationErrorDetail} for more information
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String title,
        String summary,
        List<ApiErrorDetail> errors
) implements Serializable {
}