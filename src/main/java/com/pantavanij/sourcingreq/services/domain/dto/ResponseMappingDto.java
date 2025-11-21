package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Data;

@Data
public class ResponseMappingDto {
    private String objectKey;
    private String valueKey;
    private String[] labelKey;
    private String nameKey;
}
