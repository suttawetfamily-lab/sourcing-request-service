package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import lombok.Data;

@Data
public class ApiResponseStatus {
    private String code;
    private String description;

    public ApiResponseStatus() {
        this.code = ApiMessage.I1001.name();
        this.description = ApiMessage.I1001.description();
    }

    public ApiResponseStatus(ApiMessage apiMessage) {
        this.code = apiMessage.name();
        this.description = apiMessage.description();
    }

    public ApiResponseStatus(ApiMessage apiMessage, String description) {
        this.code = apiMessage.name();
        this.description = description;
    }

    public ApiResponseStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String toString() {
        return this.code + ": " + description;
    }
}
