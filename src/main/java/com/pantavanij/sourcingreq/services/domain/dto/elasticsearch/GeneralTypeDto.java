package com.pantavanij.sourcingreq.services.domain.dto.elasticsearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeneralTypeDto {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("generalTypeCode")
    private String generalTypeCode;

    @JsonProperty("generalName")
    private String generalName;

    @JsonProperty("generalDescription")
    private String generalDescription;

    @JsonProperty("generalValue")
    private String generalValue;

    @JsonProperty("generalOrderNo")
    private Integer generalOrderNo;
}
