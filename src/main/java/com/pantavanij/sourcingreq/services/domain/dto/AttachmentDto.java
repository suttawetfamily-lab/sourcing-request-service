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
public class AttachmentDto {
    private Long recId;
    private Integer tenantId;
    private String fileId;
    private String fileName;
    private Integer fileSize;
    private String fileGroup;
    private Integer statusId;
    private String fileURL;
    private String createdBy;
    private String createdByName;
    private Timestamp createdDate;
}
