package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ReviewerCommentRequest {
    @NotNull(message = "recId must not be null or empty" )
    @Max(value = 9223372036854775807L ,message = "recId exceed limit {value} value")
    @Min(value = 0 ,message = "recId exceed limit {value} value")
    private Long recId;

    @NotNull(message = "requestId must not be null or empty" )
    @Max(value = 9223372036854775807L ,message = "requestId exceed limit {value} value")
    @Min(value = 0 ,message = "requestId exceed limit {value} value")
    private Long requestId;
    
    @Size(max = 500 ,message = "comment exceed limit {max} chars")
    private String comment;


}
