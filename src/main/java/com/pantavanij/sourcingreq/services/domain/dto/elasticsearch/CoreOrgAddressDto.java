package com.pantavanij.sourcingreq.services.domain.dto.elasticsearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoreOrgAddressDto {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("seqNo")
    private Integer seqNo;

    @JsonProperty("address")
    private AddressDto address;

    @JsonProperty("addressType")
    private GeneralTypeDto addressType;
}
