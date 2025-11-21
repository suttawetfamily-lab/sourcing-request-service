package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class RequesterDto {
    private Long recId;
    private String userId;
    private String loginId;
    private String requesterName;
    private String email;
    private String mobilePhone;
    private String phone;
    private Integer sequence;
    private boolean isDefault;
    private boolean active;
    private Timestamp createdDate;
    private String createdBy;
}
