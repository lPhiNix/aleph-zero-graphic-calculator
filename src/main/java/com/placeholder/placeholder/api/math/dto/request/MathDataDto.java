package com.placeholder.placeholder.api.math.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode
public record MathDataDto(
    int decimals,
    String origin,
    String bound
) {}
