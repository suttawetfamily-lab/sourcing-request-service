package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class RequestDeptApproverRequest {

    @NotNull(message = "requestId must not be null or empty" )
    @Max(value = 9223372036854775807L ,message = "requestId exceed limit {value} value")
    @Min(value = 0 ,message = "requestId exceed limit {value} value")
    private Long requestId;

    private List<String> approvers;
}
