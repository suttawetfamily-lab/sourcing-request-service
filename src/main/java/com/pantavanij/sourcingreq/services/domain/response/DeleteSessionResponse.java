package com.pantavanij.sourcingreq.services.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DeleteSessionResponse {
    @JsonProperty("isError")
    boolean isError;
    String msg;
}
