package com.pantavanij.sourcingreq.services.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SupplierWebWorkSearchByTPShortNameRequest {
    @JsonProperty("invitationCode")
    private String invitationCode;

    @NotBlank
    @JsonProperty("TPShortName")
    private String TPShortName;

}
