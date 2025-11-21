package com.pantavanij.sourcingreq.services.domain.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class WorkflowInstanceSaveResponse {
    private ApiResponseStatus status;

    private Data data;

    @Getter
    @Setter
    public static class Data {
        private Long workflowInstanceId;
    }
}
