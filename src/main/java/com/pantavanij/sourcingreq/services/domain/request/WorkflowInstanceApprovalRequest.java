package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

@Data
public class WorkflowInstanceApprovalRequest {
    private Long instanceApproverId;
    private String referApproverId;
    private String remark;
}
