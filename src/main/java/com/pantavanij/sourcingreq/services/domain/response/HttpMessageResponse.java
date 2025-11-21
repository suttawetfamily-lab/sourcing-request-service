package com.pantavanij.sourcingreq.services.domain.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpMessageResponse {
    private Status status;
    private Object errorMessage;
    private Object data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Status {
        private String code;
        private String description;
    }
}
