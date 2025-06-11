package com.alephzero.alephzero.db.basicdto;

import java.io.Serializable;
import java.util.Map;


public record UserPreferenceDto(
        Integer id,
        UserDto user,
        Map<String, Object> userPreferences,
        Map<String, Object> canvasPreferences
) implements Serializable {
}