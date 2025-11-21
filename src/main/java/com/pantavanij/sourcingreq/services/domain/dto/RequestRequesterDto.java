package com.pantavanij.sourcingreq.services.domain.dto;

import com.pantavanij.sourcingreq.services.domain.dto.requester.RequesterRequestDto;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class RequestRequesterDto {
    private Long recId;
    private RequesterRequestDto request;
    private RequesterDto requester;
    private String comment;
    private boolean read;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;

    private String createdByName;
}
