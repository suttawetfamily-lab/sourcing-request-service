package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.dto.workflow.WorkflowInstanceData;
import lombok.Data;

@Data
public class WorkflowDefaultApproverResponse {
    private ApiResponseStatus status;

//    private WorkflowDto data;
    private WorkflowInstanceData data;

    public WorkflowDefaultApproverResponse() {
    }
}
