package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.workflow.WorkflowInstanceData;
import com.pantavanij.sourcingreq.services.domain.request.WorkflowInstanceRequest;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponseStatus;
import com.pantavanij.sourcingreq.services.domain.response.WorkflowInstanceSaveResponse;

public interface WorkflowService {
    WorkflowInstanceSaveResponse generateInstance(Long workflowId,
                                                  WorkflowInstanceRequest request);

    ApiResponseStatus startWorkflow(Long workflowInstanceId);

    WorkflowInstanceData getWorkflowApprovers(Long workflowId);

}
