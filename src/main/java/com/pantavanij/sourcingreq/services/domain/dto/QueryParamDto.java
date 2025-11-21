package com.pantavanij.sourcingreq.services.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class QueryParamDto {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, String> searchTerm;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<SearchFormMappingDto> searchFormMapping;
}
