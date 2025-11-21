package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.WorkflowInstApproverDto;
import com.pantavanij.sourcingreq.services.domain.request.WorkflowInstanceApprovalRequest;
import com.pantavanij.sourcingreq.services.domain.request.WorkflowInstanceApproverCriteria;
import com.pantavanij.sourcingreq.services.domain.response.WorkflowInstanceApprovalResponse;
import org.springframework.data.domain.Page;

public interface WorkflowInstanceApprovalService {
    Page<WorkflowInstApproverDto> getByCriteria(WorkflowInstanceApproverCriteria criteria);

    WorkflowInstanceApprovalResponse approve(Long workflowInstanceId, WorkflowInstanceApprovalRequest request);

    WorkflowInstanceApprovalResponse reject(Long workflowInstanceId, WorkflowInstanceApprovalRequest request);

    WorkflowInstanceApprovalResponse cancel(Long workflowInstanceId, WorkflowInstanceApprovalRequest request, String remark);
}
