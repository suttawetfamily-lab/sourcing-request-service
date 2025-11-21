package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

import java.util.List;

@Data
public class WorkflowInstanceRequest {
    private List<WorkflowInstanceParamRequest> params;
    private String refDocumentId;
    private Boolean isStartImmediately;
}
