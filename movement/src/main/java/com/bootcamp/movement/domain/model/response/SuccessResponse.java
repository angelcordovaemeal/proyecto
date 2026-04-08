package com.bootcamp.movement.domain.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuccessResponse<T> {

    private String timestamp;
    private Integer status;
    private String message;
    private String path;
    private T data;

    public static <T> SuccessResponse<T> of(T data,
                                            String message,
                                            String path,
                                            int status) {
        return SuccessResponse.<T>builder()
                .timestamp(Instant.now().toString())
                .status(status)
                .message(message)
                .path(path)
                .data(data)
                .build();
    }
}