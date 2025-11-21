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
public class TenantConfigDto {
    private Integer recId;
    private String topic;
    private String section;
    private String name;
    private String value;
    private Integer sequence;
    private String description;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
}
