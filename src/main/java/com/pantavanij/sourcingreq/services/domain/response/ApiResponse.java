package com.pantavanij.sourcingreq.services.domain.response;

import lombok.Data;

@Data
public class ApiResponse<T> {

    private ApiResponseStatus status;

    private T data;

    public ApiResponse(T data) {
        this.data = data;
        this.status = new ApiResponseStatus();
    }

    public ApiResponse(T data, ApiResponseStatus status) {
        this.data = data;
        this.status = status;
    }
}
