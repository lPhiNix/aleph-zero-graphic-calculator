package com.placeholder.placeholder.api.math.dto.request;

import com.placeholder.placeholder.api.math.validation.symja.annotations.ValidDecimals;
import com.placeholder.placeholder.api.math.validation.symja.annotations.ValidOriginAndBound;

@ValidOriginAndBound
public record MathDataDto(
    @ValidDecimals int decimals,
    String origin,
    String bound
) {}
