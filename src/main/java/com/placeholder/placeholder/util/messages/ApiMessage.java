package com.placeholder.placeholder.util.messages;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.placeholder.placeholder.util.enums.AppCode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiMessage<T>(
        @NotNull @PositiveOrZero int status,
        @NotNull String code,
        String message,
        LocalDateTime timestamp,
        String path,
        T content
)
{
    /**
     * Class constructor for Api messages
     * @param code enum holding the HTTP status code, and the APP code
     * @param message optional message
     * @param path  endpoint URL
     * @param content content of the message
     */
    public ApiMessage(AppCode code, String message, String path, T content) {
        this(code.getStatus().value(), code.value(), message, LocalDateTime.now(), path, content);
    }
}