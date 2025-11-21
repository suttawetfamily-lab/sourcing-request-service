package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TenantSourcingStatusDto {
    private Integer recId;
    private OptionDto sourcingStatusObj;
    private String sourcingStatusCode;
    private String sourcingStatusDescription;
    private String purchaserOwner;
    private String purchaserNotOwner;
    private String requester;
    private String reviewer;
    private String approver;
    private String remark;
    private boolean isDefault;
    private boolean active;
    private Integer sequence;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
}
