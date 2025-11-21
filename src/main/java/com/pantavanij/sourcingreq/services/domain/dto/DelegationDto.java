package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class DelegationDto {
    private Long recId;
    private Timestamp startDate;
    private Timestamp endDate;
    private boolean noExpire;
    private String delegateeBy;
    private String delegatorBy;
    private DelegationStatusDto delegationStatus;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
    private String cancelReason;
    private String delegateeName;
    private String delegateeEmail;
    private String delegateePhone;
    private String delegateeMobilePhone;
    private Timestamp cancelDate;
    private String cancelBy;
    private String cancelByName;
}
