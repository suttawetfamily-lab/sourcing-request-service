package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.*;

import java.sql.*;

@Data
public class ReportLineDto {
    private Long recId;
    private String userId;
    private String loginId;
    private String reportLineName;
    private String email;
    private String mobilePhone;
    private String phone;
    private Integer sequence;
    private boolean isDefault;
    private boolean active;
    private Timestamp createdDate;
    private String createdBy;
}
