package com.cloudtone31.global.api;

import com.fasterxml.jackson.annotation.JsonInclude;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        T data,
        String message
) {
    public static <T> ApiResponse<T> ok(T data, String message) {
        return new ApiResponse<>(true, data, message);
    }

    public static ApiResponse<?> ok(String message) {
        return new ApiResponse<>(true, null, message);
    }

    public static ApiResponse<?> fail(String message) {
        return new ApiResponse<>(false, null, message);
    }
}