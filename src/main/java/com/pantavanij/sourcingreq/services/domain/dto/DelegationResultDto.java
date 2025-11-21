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
public class DelegationResultDto {
    private Long recId;
    private Timestamp startDate;
    private Timestamp endDate;
    private String period;
    private String delegatedUser;
    private DelegationStatusDto delegationStatus;
    private String cancelReason;
    private String delegateeName;
    private String delegateeEmail;
    private String delegateePhone;
    private String delegateeMobilePhone;
    private Timestamp cancelDate;
    private String cancelBy;
    private String cancelByName;
}
