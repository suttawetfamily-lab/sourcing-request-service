package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.*;

@Data
public class TenantSectionDetailFieldNameDto {
    private Long id;
    private String name;
    private String fieldName;
    private Long value;
    private String label;
    private String reportLabel;
    private DataSourceObjDto dataSource;
    private String type;
}
