package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.dto.DefaultApproverDto;
import lombok.Data;

@Data
public class DefaultApproverResponse {

    private ApiResponseStatus status;

    private DefaultApproverDto data;

    public DefaultApproverResponse(DefaultApproverDto data) {
        this.data = data;
        this.status = new ApiResponseStatus();
    }
}
