package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class RequestReviewerSourcingStatusRequest {
    @NotNull(message = "requestId must not be null or empty" )
    @Max(value = 9223372036854775807L ,message = "requestId exceed limit {value} value")
    @Min(value = 0 ,message = "requestId exceed limit {value} value")
    private  Long requestId;

    @NotNull(message = "sourcingStatusId must not be null or empty" )
    @Max(value = 9223372036854775807L ,message = "sourcingStatusId exceed limit {value} value")
    @Min(value = 0 ,message = "sourcingStatusId exceed limit {value} value")
    private Integer sourcingStatusId;

    @NotNull(message = "page must not be null or empty" )
    @Max(value = 9223372036854775807L ,message = "page exceed limit {value} value")
    @Min(value = 0 ,message = "page exceed limit {value} value")
    private Integer page;

    @NotNull(message = "size must not be null or empty" )
    @Max(value = 9223372036854775807L ,message = "size exceed limit {value} value")
    @Min(value = 0 ,message = "size exceed limit {value} value")
    private Integer size;


}
