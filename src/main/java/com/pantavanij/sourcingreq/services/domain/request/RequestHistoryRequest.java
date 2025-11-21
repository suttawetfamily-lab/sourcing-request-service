package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RequestHistoryRequest {
    private Long recId;
    @NotNull
    private Long requestId;
    @NotNull
    private Integer tenantId;
    @NotNull
    private Integer activityId;
    private String remark;
}
