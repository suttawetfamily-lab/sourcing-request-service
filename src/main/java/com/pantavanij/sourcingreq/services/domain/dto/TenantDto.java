package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TenantDto {
    private Long tenantId;
    private String tenantCode;
    private String tenantName;
    private String tenantDescription;
    private String createdBy;
    private Timestamp createdDate;
}
