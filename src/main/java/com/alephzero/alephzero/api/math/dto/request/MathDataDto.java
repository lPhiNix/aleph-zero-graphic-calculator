package com.alephzero.alephzero.api.math.dto.request;

import com.alephzero.alephzero.api.math.validation.symja.annotations.ValidDecimals;

/**
 * Data Transfer Object representing additional data needed for
 * mathematical evaluation such as decimals precision and domain bounds.
 * <p>
 * Includes validation annotations for decimals and bounds.
 * </p>
 *
 * @param decimals Number of decimal places for numeric calculations
 * @param origin Start of the domain for plotting or evaluation
 * @param bound End of the domain for plotting or evaluation
 */

public record MathDataDto(
        @ValidDecimals int decimals,
        String origin,
        String bound
) {}
