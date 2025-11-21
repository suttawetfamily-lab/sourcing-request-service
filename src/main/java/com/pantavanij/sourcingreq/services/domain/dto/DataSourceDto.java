package com.pantavanij.sourcingreq.services.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class DataSourceDto {
    private Integer recId;
    private String label;
    private String method;
    private String name;
    private String path;
    private Integer value;

    @JsonIgnore
    private String queryParameters;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer minimumSearch;

    @JsonIgnore
    private String objectKey;

    @JsonIgnore
    private String valueKey;

    @JsonIgnore
    private String labelKey;

    @JsonIgnore
    private String nameKey;

    @JsonIgnore
    private String formName;

    @JsonIgnore
    private String queryName;

    @JsonIgnore
    private String formValue;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private QueryParamDto queryParams;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private ResponseMappingDto responseMapping;
}
