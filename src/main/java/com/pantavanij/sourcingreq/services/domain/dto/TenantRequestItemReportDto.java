package com.pantavanij.sourcingreq.services.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantRequestItemReportDto {
    private Integer recId;
    private RequestItemReportDto requestItemReport;
    @JsonProperty("tenantSectionDetailObj")
    private TenantSectionDetailFieldNameDto tenantSectionDetail;
    private Boolean active;
    @JsonProperty("default")
    private Boolean isDefault;
    private Integer sequence;
    private Timestamp createdDate;
    private String createdBy;
    private Timestamp updatedDate;
    private String updatedBy;
    private Integer templateId;
}
