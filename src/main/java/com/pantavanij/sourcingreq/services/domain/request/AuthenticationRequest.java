package com.pantavanij.sourcingreq.services.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
public class AuthenticationRequest {
    @NotBlank
    @JsonProperty("grant_type")
    private String grantType;
    @NotBlank
    @JsonProperty("auth_code")
    private String authCode;
    @NotEmpty
    private String pathUrl;
}
