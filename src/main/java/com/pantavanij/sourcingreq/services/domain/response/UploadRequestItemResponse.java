package com.pantavanij.sourcingreq.services.domain.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pantavanij.sourcingreq.services.domain.dto.RequestItemV2Dto;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UploadRequestItemResponse {
    private  boolean valid;
    private List<UploadMessageResponse> messageResponses;
    private List<RequestItemV2Dto> itemV2DtoList;
}
