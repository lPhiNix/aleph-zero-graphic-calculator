package com.alephzero.alephzero.db.basicdto;

import java.io.Serializable;
import java.util.Map;

@Deprecated(since = "NOT YET IMPLEMENTED")
public record UserPreferenceDto(
        Integer id,
        UserDto user,
        Map<String, Object> userPreferences,
        Map<String, Object> canvasPreferences
) implements Serializable {
}