package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RequestProjectRequest {
    @NotNull(message = "requestId must not be null or empty" )
    @Max(value = 9223372036854775807L ,message = "requestId exceed limit {value} value")
    @Min(value = 0 ,message = "requestId exceed limit {value} value")
    private Long requestId;

    @NotNull(message = "projectId must not be null or empty" )
    @Max(value = 2147483647 ,message = "projectId exceed limit {value} value")
    @Min(value = 0 ,message = "projectId exceed limit {value} value")
    private Integer projectId;

    @Size(max = 100 ,message = "projectCode exceed limit {max} chars")
    private String projectCode;

    @Size(max = 500 ,message = "projectName exceed limit {max} chars")
    private String projectName;
}
