package com.placeholder.placeholder.util.messages;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.placeholder.placeholder.util.enums.AppCode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiMessage<T>(
        @NotNull @PositiveOrZero int status,
        @NotNull String code,
        String message,
        LocalDate timestamp,
        T content
)
{
    /**
     * Class constructor for Api messages
     * @param code enum holding the HTTP status code, and the APP code
     * @param message optional message
     * @param content content of the message
     */
    public ApiMessage(AppCode code, String message, T content) {
        this(code.getStatus().value(), code.value(), message, LocalDate.now(), content);
    }
}
