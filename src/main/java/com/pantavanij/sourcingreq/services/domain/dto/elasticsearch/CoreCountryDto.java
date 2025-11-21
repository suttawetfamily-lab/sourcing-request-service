package com.pantavanij.sourcingreq.services.domain.dto.elasticsearch;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoreCountryDto {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("countryName")
    private String countryName;

    @JsonProperty("countryNameCode")
    private String countryNameCode;

    @JsonProperty("countryCode")
    private String countryCode;

    @JsonProperty("isEnglishOffcial")
    private Boolean isEnglishOffcial;
}
