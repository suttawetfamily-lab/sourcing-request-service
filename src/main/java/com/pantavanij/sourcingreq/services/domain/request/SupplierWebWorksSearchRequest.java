package com.pantavanij.sourcingreq.services.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SupplierWebWorksSearchRequest {
    @NotBlank
    @JsonProperty("searching")
    private String searching;

    @JsonProperty("invitationCode")
    private String invitationCode;

    @JsonProperty("limit")
    private int limit;

}
