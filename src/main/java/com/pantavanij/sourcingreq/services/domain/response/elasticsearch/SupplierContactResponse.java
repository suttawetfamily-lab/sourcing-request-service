package com.pantavanij.sourcingreq.services.domain.response.elasticsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pantavanij.sourcingreq.services.domain.dto.elasticsearch.SupplierContactsDto;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SupplierContactResponse {
    @JsonProperty("data")
    private List<SupplierContactsDto> data;

    @JsonProperty("errors")
    private List<Map<String, String>> errors;

}
