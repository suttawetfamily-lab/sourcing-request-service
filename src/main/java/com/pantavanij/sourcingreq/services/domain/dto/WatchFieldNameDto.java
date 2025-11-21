package com.pantavanij.sourcingreq.services.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WatchFieldNameDto {

    private List<WatchFieldDto> fields;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<WatchDataFieldDto> defaultDataFromProps;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<WatchDataFieldDto> updateDataFromProps;

    private String resetValue;
}
