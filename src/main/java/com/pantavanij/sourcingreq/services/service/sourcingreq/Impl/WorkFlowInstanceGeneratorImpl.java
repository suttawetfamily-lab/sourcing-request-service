package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.WorkflowParam;
import com.pantavanij.sourcingreq.services.domain.dto.WorkflowParamERFX;
import com.pantavanij.sourcingreq.services.domain.request.WorkflowInstanceParamRequest;
import com.pantavanij.sourcingreq.services.domain.request.WorkflowInstanceRequest;
import com.pantavanij.sourcingreq.services.domain.response.WorkflowInstanceSaveResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.service.sourcingreq.WorkFlowInstanceGenerator;
import com.pantavanij.sourcingreq.services.service.sourcingreq.WorkflowService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WorkFlowInstanceGeneratorImpl implements WorkFlowInstanceGenerator {
    @Override
    public Long generateWorkflowInstanceWithWorkflowParam(WorkflowService workflowService, Long workflowId, String refDocumentId, WorkflowParam workflowParam) {
        WorkflowInstanceRequest request = new WorkflowInstanceRequest();
        List<WorkflowInstanceParamRequest> params = setWorkflowInstanceParamRequests(workflowParam);

        request.setParams(params);
        request.setRefDocumentId(refDocumentId);
        request.setIsStartImmediately(false);
        WorkflowInstanceSaveResponse workflowInstanceSaveResponse = workflowService.generateInstance(workflowId, request);
        if (workflowInstanceSaveResponse.getData() == null) {
            throw new BusinessException(ApiMessage.E7030);
        }
        return workflowInstanceSaveResponse.getData().getWorkflowInstanceId();
    }

    @Override
    public Long generateWorkflowInstanceWithWorkflowParamERFX(WorkflowService workflowService, Long workflowId, String refDocumentId, WorkflowParamERFX workflowParam) {
        WorkflowInstanceRequest request = new WorkflowInstanceRequest();
        List<WorkflowInstanceParamRequest> params = setWorkflowInstanceParamERFXRequests(workflowParam);

        request.setParams(params);
        request.setRefDocumentId(refDocumentId);
        request.setIsStartImmediately(false);
        WorkflowInstanceSaveResponse workflowInstanceSaveResponse = workflowService.generateInstance(workflowId, request);
        if (workflowInstanceSaveResponse.getData() == null) {
            throw new BusinessException(ApiMessage.E7030);
        }
        return workflowInstanceSaveResponse.getData().getWorkflowInstanceId();
    }

    @Override
    public Long generateWorkflowInstance(WorkflowService workflowService, Long workflowId, String refDocumentId) {
        WorkflowInstanceRequest request = new WorkflowInstanceRequest();
        List<WorkflowInstanceParamRequest> params = new ArrayList<>();
        WorkflowInstanceParamRequest param = new WorkflowInstanceParamRequest("Category", "Event Organizer");
        params.add(param);
        request.setParams(params);
        request.setRefDocumentId(refDocumentId);
        request.setIsStartImmediately(false);
        WorkflowInstanceSaveResponse workflowInstanceSaveResponse = workflowService.generateInstance(workflowId, request);
        if (workflowInstanceSaveResponse.getData() == null) {
            throw new BusinessException(ApiMessage.E7030);
        }
        return workflowInstanceSaveResponse.getData().getWorkflowInstanceId();
    }

    private static List<WorkflowInstanceParamRequest> setWorkflowInstanceParamRequests(WorkflowParam workflowParam) {
        List<WorkflowInstanceParamRequest> params = new ArrayList<>();

        WorkflowInstanceParamRequest paramCate = new WorkflowInstanceParamRequest("Category", workflowParam.getCategory());
        WorkflowInstanceParamRequest paramSubCate = new WorkflowInstanceParamRequest("SubCategory", workflowParam.getSubCategory());
        WorkflowInstanceParamRequest paramType = new WorkflowInstanceParamRequest("Type", workflowParam.getType());
        WorkflowInstanceParamRequest paramAmount = new WorkflowInstanceParamRequest("Amount", workflowParam.getAmount().toString());

        params.add(paramCate);
        params.add(paramSubCate);
        params.add(paramType);
        params.add(paramAmount);
        return params;
    }

    private static List<WorkflowInstanceParamRequest> setWorkflowInstanceParamERFXRequests(WorkflowParamERFX workflowParam) {
        List<WorkflowInstanceParamRequest> params = new ArrayList<>();

        WorkflowInstanceParamRequest paramPurchaserName = new WorkflowInstanceParamRequest("PurchaserName", workflowParam.getPurchaserName());
        WorkflowInstanceParamRequest paramAmount = new WorkflowInstanceParamRequest("Amount", workflowParam.getAmount().toString());

        params.add(paramPurchaserName);
        params.add(paramAmount);
        return params;
    }
}
