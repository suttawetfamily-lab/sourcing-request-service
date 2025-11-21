package com.pantavanij.sourcingreq.services.domain.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {
    private T data;
    private String message;

    public Response(T data) {
        super();
        this.data = data;
    }

    public Response(String message) {
        super();
        this.message = message;
    }

    public Response(T data, String message) {
        super();
        this.data = data;
        this.message = message;
    }

}
