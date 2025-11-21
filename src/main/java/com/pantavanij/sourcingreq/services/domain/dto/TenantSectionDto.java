package com.pantavanij.sourcingreq.services.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.*;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TenantSectionDto {
    private Long id;
    private String createdBy;
    private String sectionName;
    private Integer step;
    private Integer sequence;
    private String type;
    private Boolean visible;
    private String updatedBy;
    private Timestamp createdDate;
    private Timestamp updatedDate;

    @JsonIgnore
    private String privilegeCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<TenantSectionDetailDto> fields;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String sectionSubTitle;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String sectionTitle;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<TenantSectionDetailDto> tenantSectionDetailList;
}
