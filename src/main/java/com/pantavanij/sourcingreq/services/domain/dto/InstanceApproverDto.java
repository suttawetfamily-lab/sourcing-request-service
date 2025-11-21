package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class InstanceApproverDto {
    private Integer sysUserId;
    private String loginId;
    private String fullName;
    private String email;
    private String mobilePhone;
    private String phone;
    private String status;
    private boolean isRequired;
    private String comment;
    private Timestamp delegatedDate;
    private String delegatedBy;
    private Timestamp forwardedDate;
    private String addedBy;
}
