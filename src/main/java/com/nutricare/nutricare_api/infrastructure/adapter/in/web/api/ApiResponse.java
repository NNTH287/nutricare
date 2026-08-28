package com.nutricare.nutricare_api.infrastructure.adapter.in.web.api;

public record ApiResponse<T>(int status, String message, T data) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(200, data);
    }

    public ApiResponse {
        if (message == null) {
            message = "";
        }
    }

    public ApiResponse(int status, T data) {
        this(status, "", data);
    }
}