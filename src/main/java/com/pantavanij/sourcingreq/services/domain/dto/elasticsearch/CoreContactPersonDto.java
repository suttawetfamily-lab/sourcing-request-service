package com.pantavanij.sourcingreq.services.domain.dto.elasticsearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoreContactPersonDto {
    @JsonProperty("fullNameLocal")
    private String fullNameLocal;

    @JsonProperty("fullNameInter")
    private String fullNameInter;

    @JsonProperty("email")
    private String email;
}
