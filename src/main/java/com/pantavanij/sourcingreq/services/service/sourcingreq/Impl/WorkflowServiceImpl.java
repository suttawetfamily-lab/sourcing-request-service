package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.WorkflowClient;
import com.pantavanij.sourcingreq.services.domain.dto.workflow.WorkflowInstanceData;
import com.pantavanij.sourcingreq.services.domain.request.WorkflowInstanceRequest;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponseStatus;
import com.pantavanij.sourcingreq.services.domain.response.WorkflowInstanceSaveResponse;
import com.pantavanij.sourcingreq.services.service.sourcingreq.WorkflowService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class WorkflowServiceImpl implements WorkflowService {

    private final WorkflowClient workflowClient;
    @Override
    public WorkflowInstanceSaveResponse generateInstance(Long workflowId, WorkflowInstanceRequest request) {
        String authHeader = "Bearer " + AppUtil.getJwtToken();
        return workflowClient.generateWorkflow(authHeader, workflowId, request);
    }

    @Override
    public ApiResponseStatus startWorkflow(Long workflowInstanceId) {
        Map<String, String> map = new HashMap<>();
        map.put("refDocumentId", null);
        String authHeader = "Bearer " + AppUtil.getJwtToken();
        return workflowClient.startWorkflow(authHeader, workflowInstanceId, map);
    }

    @Override
    public WorkflowInstanceData getWorkflowApprovers(Long workflowId) {
        String authHeader = "Bearer " + AppUtil.getJwtToken();
        return workflowClient.getWorkflowApprover(authHeader, workflowId).getData();
    }
}
