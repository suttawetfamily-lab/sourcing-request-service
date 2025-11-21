package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.dto.RequestDto;
import lombok.Data;

@Data
public class RequestResponse {

    private ApiResponseStatus status;

    private RequestDto data;

    public RequestResponse(RequestDto data) {
        this.data = data;
        this.status = new ApiResponseStatus();
    }
}
