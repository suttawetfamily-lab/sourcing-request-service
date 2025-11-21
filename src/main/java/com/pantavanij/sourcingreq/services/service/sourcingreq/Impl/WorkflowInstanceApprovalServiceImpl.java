package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.WorkflowClient;
import com.pantavanij.sourcingreq.services.domain.dto.WorkflowInstApproverDto;
import com.pantavanij.sourcingreq.services.domain.request.WorkflowInstanceApprovalRequest;
import com.pantavanij.sourcingreq.services.domain.request.WorkflowInstanceApproverCriteria;
import com.pantavanij.sourcingreq.services.domain.response.WorkflowInstanceApprovalResponse;
import com.pantavanij.sourcingreq.services.domain.response.WorkflowInstanceApproverResultPageResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.AppException;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.service.sourcingreq.WorkflowInstanceApprovalService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class WorkflowInstanceApprovalServiceImpl implements WorkflowInstanceApprovalService {

    private final WorkflowClient workflowClient;

    @Override
    public Page<WorkflowInstApproverDto> getByCriteria(WorkflowInstanceApproverCriteria criteria) {
        String authHeader = "Bearer " + AppUtil.getJwtToken();

        List<String> status;
        if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
            status = criteria.getStatus();
        } else {
            status = Arrays.asList("AWAITING", "REJECTED", "APPROVED");
        }

        WorkflowInstanceApproverResultPageResponse response =
                workflowClient.approval(authHeader,
                        (criteria.getPageNumber() - 1),
                        criteria.getPageSize(),
                        criteria.getApprovalType(),
                        criteria.getWorkflowId(),
                        criteria.getWorkflowInstanceId(),
                        status,
                        criteria.getReferApproverId(),
                        criteria.getRefDocumentId(),
                        criteria.getSortBy(),
                        criteria.getSortDirection());
        return Objects.requireNonNull(response.getData());
    }


    @Override
    public WorkflowInstanceApprovalResponse approve(Long workflowInstanceId, WorkflowInstanceApprovalRequest request) {
        if (request == null) {
            throw new BusinessException(ApiMessage.E7031,
                    String.format(ApiMessage.E7031.description(), "WorkflowInstanceApprovalRequest object data"));
        }

        if (request.getInstanceApproverId() == null) {
            throw new BusinessException(ApiMessage.E7031,
                    String.format(ApiMessage.E7031.description(), "instanceApproverId"));
        }

        if (request.getReferApproverId() == null || request.getReferApproverId().isEmpty()) {
            throw new BusinessException(ApiMessage.E7031,
                    String.format(ApiMessage.E7031.description(), "referApproverId"));
        }

        if (request.getRemark() != null && request.getRemark().length() > 1000) {
            throw new BusinessException(ApiMessage.E7032,
                    String.format(ApiMessage.E7032.description(), "remark", "1000"));
        }

        try {
            String authHeader = "Bearer " + AppUtil.getJwtToken();
            return workflowClient.approval(authHeader, workflowInstanceId, "approve", request);
        } catch (Exception ex) {
            throw new AppException(ApiMessage.E7029, String.format(ApiMessage.E7029.description(), ex.getMessage()));
        }
    }

    @Override
    public WorkflowInstanceApprovalResponse reject(Long workflowInstanceId, WorkflowInstanceApprovalRequest request) {
        if (request == null) {
            throw new BusinessException(ApiMessage.E7031,
                    String.format(ApiMessage.E7031.description(), "WorkflowInstanceApprovalRequest object data"));
        }

        if (request.getInstanceApproverId() == null) {
            throw new BusinessException(ApiMessage.E7031,
                    String.format(ApiMessage.E7031.description(), "instanceApproverId"));
        }

        if (request.getReferApproverId() == null || request.getReferApproverId().isEmpty()) {
            throw new BusinessException(ApiMessage.E7031,
                    String.format(ApiMessage.E7031.description(), "referApproverId"));
        }

        if (request.getRemark() == null || request.getRemark().isEmpty()) {
            throw new BusinessException(ApiMessage.E7031,
                    String.format(ApiMessage.E7031.description(), "remark"));
        }

        if (request.getRemark() != null && request.getRemark().length() > 1000) {
            throw new BusinessException(ApiMessage.E7032,
                    String.format(ApiMessage.E7032.description(), "remark", "1000"));
        }

        try {
            String authHeader = "Bearer " + AppUtil.getJwtToken();
            return workflowClient.approval(authHeader, workflowInstanceId, "reject", request);
        } catch (Exception ex) {
            throw new AppException(ApiMessage.E7029, String.format(ApiMessage.E7029.description(), ex.getMessage()));
        }
    }

    @Override
    public WorkflowInstanceApprovalResponse cancel(Long workflowInstanceId, WorkflowInstanceApprovalRequest request, String remark) {
        try {
            String authHeader = "Bearer " + AppUtil.getJwtToken();
            return workflowClient.cancel(authHeader, workflowInstanceId, remark, request);
        } catch (Exception ex) {
            throw new AppException(ApiMessage.E7029, String.format(ApiMessage.E7029.description(), ex.getMessage()));
        }
    }
}
