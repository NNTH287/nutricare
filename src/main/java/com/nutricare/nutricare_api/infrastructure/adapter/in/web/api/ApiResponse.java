package com.nutricare.nutricare_api.infrastructure.adapter.in.web.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;

public record ApiResponse<T>(String message, T data) {

    private static <T> ApiResponse<T> of(String message, T data) {
        return new ApiResponse<>(message, data);
    }

    // success helpers
    public static <T> ResponseEntity<ApiResponse<T>> ok(T data) {
        return ResponseEntity.ok(of(null, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {
        return ResponseEntity.ok(of(message, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(URI location, T data) {
        return ResponseEntity.created(location).body(of(null, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(URI location, String message, T data) {
        return ResponseEntity.created(location).body(of(message, data));
    }

    public static ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }

    // error helpers
    public static ResponseEntity<ApiResponse<Void>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(of(message, null));
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(HttpStatus status, String message, T data) {
        return ResponseEntity.status(status).body(of(message, data));
    }
}