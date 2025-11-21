package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.WorkflowParam;
import com.pantavanij.sourcingreq.services.domain.dto.WorkflowParamERFX;

public interface WorkFlowInstanceGenerator {
    Long generateWorkflowInstanceWithWorkflowParam(WorkflowService workflowService, Long workflowId, String refDocumentId, WorkflowParam params);
    Long generateWorkflowInstanceWithWorkflowParamERFX(WorkflowService workflowService, Long workflowId, String refDocumentId, WorkflowParamERFX params);
    Long generateWorkflowInstance(WorkflowService workflowService, Long workflowId, String refDocumentId);
}
