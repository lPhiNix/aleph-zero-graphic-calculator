package com.alephzero.alephzero.commons.messages.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.alephzero.alephzero.commons.messages.dto.error.details.ApiErrorDetail;
import com.alephzero.alephzero.commons.messages.dto.error.details.ValidationErrorDetail;
import com.alephzero.alephzero.infrastructure.config.enums.AppCode;

import java.io.Serializable;
import java.util.List;

/**
 * Standard DTO response message for errors
 *
 * @param appCode string value from {@link AppCode}
 * @param title   a detailed description of the error.
 * @param errors  optional value, contains a list of
 *                {@link ValidationErrorDetail} for more information
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
		String appCode,
		String title,
		List<? extends ApiErrorDetail> errors) implements Serializable {
}
