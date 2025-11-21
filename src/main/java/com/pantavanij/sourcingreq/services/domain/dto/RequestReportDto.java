package com.pantavanij.sourcingreq.services.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class RequestReportDto {
    private Integer recId;
    private RequestTenantDto tenant;
    private String code;
    private String name;
    private String privilegeCode;
    private Integer sequence;
    private boolean isDefault;
    private boolean active;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
    private List<TenantRequestReportDto> tenantRequestReportList;
}
