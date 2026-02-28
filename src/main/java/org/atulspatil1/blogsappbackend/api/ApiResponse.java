package org.atulspatil1.blogsappbackend.api;

import java.time.Instant;

public record ApiResponse<T>(
        boolean success,
        int status,
        String message,
        T data,
        Object errors,
        Instant timestamp,
        String path
) {
    public static <T> ApiResponse<T> success(int status, String message, T data, String path) {
        return new ApiResponse<>(true, status, message, data, null, Instant.now(), path);
    }

    public static ApiResponse<Void> error(int status, String message, Object errors, String path) {
        return new ApiResponse<>(false, status, message, null, errors, Instant.now(), path);
    }
}
