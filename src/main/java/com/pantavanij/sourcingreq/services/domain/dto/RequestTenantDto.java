package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class RequestTenantDto {
    private Integer recId;
    private String code;
    private String name;
    private String description;
}
