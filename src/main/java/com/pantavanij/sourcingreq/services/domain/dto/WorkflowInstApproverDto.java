package com.pantavanij.sourcingreq.services.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class WorkflowInstApproverDto {
    @NotNull
    protected String projectId;
    @NotNull
    protected String tenantId;
    private Long recordId;
    private String referApproverId;
    private Boolean isRequired;
    private String remark;
    @Enumerated(EnumType.STRING)
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date createdAt;
    private String createdBy;
    private String createdByFullName;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date updatedAt;
    private String updatedBy;
    private Long workflowInstanceStepId;
    private String refDocumentId;
    private Long workflowInstanceId;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date receiveDate;
}
