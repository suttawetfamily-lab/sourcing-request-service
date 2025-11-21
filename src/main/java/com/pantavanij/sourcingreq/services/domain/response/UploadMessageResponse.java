package com.pantavanij.sourcingreq.services.domain.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UploadMessageResponse {
    private ApiMessage code;
    private String column;
    private Integer row;
    private String description;
}
