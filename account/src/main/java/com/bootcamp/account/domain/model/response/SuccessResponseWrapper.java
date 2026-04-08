package com.bootcamp.account.domain.model.response;

import lombok.Data;

@Data
public class SuccessResponseWrapper<T> {
    private String timestamp;
    private int status;
    private String message;
    private String path;
    private T data;
}
