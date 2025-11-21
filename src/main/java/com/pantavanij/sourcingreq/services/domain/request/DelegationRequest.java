package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class DelegationRequest {
    private Long recId;
    private Timestamp startDate;
    private Timestamp endDate;
    private boolean noExpire;
    private String delegateeBy;
    private String delegatorBy;
    private Long delegationStatusId;
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
