package com.bootcamp.credit.domain.model.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SuccessResponse<T> {

    private LocalDateTime timestamp;
    private int status;
    private String message;
    private String path;
    private T data;

    public static <T> SuccessResponse<T> of(T data, String message, String path, int status) {
        return SuccessResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .message(message)
                .path(path)
                .data(data)
                .build();
    }
}