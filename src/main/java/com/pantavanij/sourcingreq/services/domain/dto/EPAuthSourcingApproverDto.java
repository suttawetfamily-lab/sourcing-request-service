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
public class EPAuthSourcingApproverDto {
    Long requestDeptApproverId;
    Integer sysUserId;
    String loginId;
    String fullName;
    String email;
    String mobilePhone;
    String phone;
    String timezone;
    String comment;
    Timestamp commentDate;
    String AddedBy;
    String department;
    String name;
    Boolean isDefault;
}
