package com.pantavanij.sourcingreq.services.domain.dto;

import com.pantavanij.sourcingreq.services.domain.dto.reviewer.ReviewerRequestDto;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class RequestReviewerDto {
    private Long recId;
    private ReviewerRequestDto request;
    private ReviewerDto reviewer;
    private String comment;
    private boolean read;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;

    private String createdByName;
}
