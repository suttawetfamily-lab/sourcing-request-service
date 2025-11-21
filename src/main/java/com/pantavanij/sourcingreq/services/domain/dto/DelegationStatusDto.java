package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DelegationStatusDto {
    private Long recId;
    private String name;
    private String description;
    private boolean canCancel;
    private String createdBy;
    private Timestamp createdDate;
}
