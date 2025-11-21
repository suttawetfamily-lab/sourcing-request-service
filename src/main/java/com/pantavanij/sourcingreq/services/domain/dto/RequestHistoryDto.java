package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class RequestHistoryDto {
    private Long recId;
    private ActivityDto activity;
    private String remark;
    private String createdBy;
    private String createdByName;
    private Timestamp createdDate;
}
