package com.placeholder.placeholder.util.messages.builders;

import com.placeholder.placeholder.util.messages.dto.ApiMessage;
import com.placeholder.placeholder.util.messages.dto.MessageContent;

public class ApiResponseBuilder<T extends MessageContent> {
    private int status;
    private String code;
    private String message;
    private String path;

    private T content;

    public static <T extends MessageContent>ApiResponseBuilder<T> builder() {
        return new ApiResponseBuilder<>();
    }

    public ApiResponseBuilder<T> status(int status) {
        this.status = status;
        return this;
    }

    public ApiResponseBuilder<T> code(String code) {
        this.code = code;
        return this;
    }

    public ApiResponseBuilder<T> message(String message) {
        this.message = message;
        return this;
    }

    public ApiResponseBuilder<T> path(String path) {
        this.path = path;
        return this;
    }

    public ApiResponseBuilder<T> content(T content) {
        this.content = content;
        return this;
    }

    public ApiMessage<T> build(){
        return new ApiMessage<>(status, code, message, path, content);
    }
}