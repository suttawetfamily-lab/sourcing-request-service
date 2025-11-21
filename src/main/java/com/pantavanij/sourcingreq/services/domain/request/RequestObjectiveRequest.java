package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RequestObjectiveRequest {
    @NotNull(message = "requestId must not be null or empty" )
    @Max(value = 9223372036854775807L ,message = "requestId exceed limit {value} value")
    @Min(value = 0 ,message = "requestId exceed limit {value} value")
    private Long requestId;

    @NotNull(message = "objectiveId must not be null or empty" )
    @Max(value = 2147483647 ,message = "objectiveId exceed limit {value} value")
    @Min(value = 0 ,message = "objectiveId exceed limit {value} value")
    private Integer objectiveId;

    @Size(max = 100 ,message = "objectiveCode exceed limit {max} chars")
    private String objectiveCode;

    @Size(max = 500 ,message = "objectiveName exceed limit {max} chars")
    private String objectiveName;
}
