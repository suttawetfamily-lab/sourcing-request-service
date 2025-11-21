package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import lombok.Data;

@Data
public class ApiErrorResponse {
    private ApiResponseStatus status;

    public ApiErrorResponse(String msgCode) {
        String description = ApiMessage.description(msgCode);
        if ("".equals(description)) {
            description = msgCode;
        }
        this.status = new ApiResponseStatus(msgCode, description);
    }

    public ApiErrorResponse(String msgCode, String description) {

        this.status = new ApiResponseStatus(msgCode, description);
    }

    public ApiErrorResponse(ApiMessage apiMessage) {
        this.status = new ApiResponseStatus(apiMessage);
    }

    public ApiErrorResponse(ApiMessage apiMessage, String description) {
        this.status = new ApiResponseStatus(apiMessage, description);
    }
}