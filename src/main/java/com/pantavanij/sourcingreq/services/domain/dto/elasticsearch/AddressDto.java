package com.pantavanij.sourcingreq.services.domain.dto.elasticsearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressDto {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("addressKeyId")
    private Integer addressKeyId;

    @JsonProperty("houseNoLocal")
    private String houseNoLocal;

    @JsonProperty("houseNoInter")
    private String houseNoInter;

    @JsonProperty("villageNoLocal")
    private String villageNoLocal;

    @JsonProperty("villageNoInter")
    private String villageNoInter;

    @JsonProperty("laneLocal")
    private String laneLocal;

    @JsonProperty("laneInter")
    private String laneInter;

    @JsonProperty("roadLocal")
    private String roadLocal;

    @JsonProperty("roadInter")
    private String roadInter;

    @JsonProperty("subDistrictLocal")
    private String subDistrictLocal;

    @JsonProperty("subDistrictInter")
    private String subDistrictInter;

    @JsonProperty("cityLocal")
    private String cityLocal;

    @JsonProperty("cityInter")
    private String cityInter;

    @JsonProperty("stateLocal")
    private String stateLocal;

    @JsonProperty("stateInter")
    private String stateInter;

    @JsonProperty("countryCode")
    private String countryCode;

    @JsonProperty("postalCode")
    private String postalCode;
}
