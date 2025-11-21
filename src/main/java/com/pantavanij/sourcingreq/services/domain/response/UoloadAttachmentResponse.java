package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.dto.AttachmentDto;
import lombok.Data;

@Data
public class UoloadAttachmentResponse {

    private ApiResponseStatus status;

    private AttachmentDto data;

    public UoloadAttachmentResponse(AttachmentDto data) {
        this.data = data;
        this.status = new ApiResponseStatus();
    }
}
