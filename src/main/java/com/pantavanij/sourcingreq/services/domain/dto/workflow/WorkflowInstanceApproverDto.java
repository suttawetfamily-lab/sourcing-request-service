package com.pantavanij.sourcingreq.services.domain.dto.workflow;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@Data
public class WorkflowInstanceApproverDto {
    private String projectId;
    private String tenantId;
    private Long recordId;
    private String referApproverId;
    private Long workflowApproverId;
    private Boolean isRequired;
    private String status;
    private String mark;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    @CreatedDate
    private Date receiveDate;
    private Long workflowInstanceStepId;
    private Long referDelegatedApproverId;
    private Long reNotifyCount;
}
