package com.pantavanij.sourcingreq.services.domain.response.elasticsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pantavanij.sourcingreq.services.domain.dto.elasticsearch.SupplierWebworksDto;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SupplierWebWorksResponse {
    @JsonProperty("data")
    private List<SupplierWebworksDto> data;

    @JsonProperty("errors")
    private List<Map<String, String>> errors;

}
