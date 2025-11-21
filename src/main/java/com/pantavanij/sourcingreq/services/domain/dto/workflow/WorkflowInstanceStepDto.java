package com.pantavanij.sourcingreq.services.domain.dto.workflow;

import lombok.Data;

import java.util.List;

@Data
public class WorkflowInstanceStepDto {
    private String projectId;
    private String tenantId;
    private Long recordId;
    private Long workflowStepId;
    private String stepName;
    private String status;
    private Integer orderNumber;
    private Long workflowInstanceStageId;
    private Integer numberOfApproveRequired;
    private String approvalType;
    private List<WorkflowInstanceApproverDto> instanceApprovers;
}
