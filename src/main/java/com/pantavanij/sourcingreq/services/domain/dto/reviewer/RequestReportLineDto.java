package com.pantavanij.sourcingreq.services.domain.dto.reviewer;

import com.pantavanij.sourcingreq.services.domain.dto.ReportLineDto;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class RequestReportLineDto {
    private Long recId;
    private Long requestId;
    private ReportLineDto reportLine;
    private String comment;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
}
