package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.dto.WorkflowInstApproverDto;
import lombok.Data;

@Data
public class WorkflowInstanceApproverResultPageResponse {
    private ApiResponseStatus status;

    private RestResponsePage<WorkflowInstApproverDto> data;

    public WorkflowInstanceApproverResultPageResponse() {
    }
}
