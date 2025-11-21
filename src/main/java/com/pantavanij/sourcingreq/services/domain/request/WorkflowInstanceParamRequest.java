package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

@Data
public class WorkflowInstanceParamRequest {
    String name;
    String value;

    public WorkflowInstanceParamRequest(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
