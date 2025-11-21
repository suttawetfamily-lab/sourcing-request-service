package com.pantavanij.sourcingreq.services.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VerifySessionResponse {
    @JsonProperty("isError")
    boolean isError;
    String errorMsg;
}
