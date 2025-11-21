package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RequestItemPurposeRequest {
    @NotNull(message = "requestItemId must not be null or empty" )
    @Max(value = 9223372036854775807L ,message = "requestItemId exceed limit {value} value")
    @Min(value = 0 ,message = "requestItemId exceed limit {value} value")
    private Long requestItemId;

    @NotNull(message = "purposeId must not be null or empty" )
    @Max(value = 2147483647 ,message = "purposeId exceed limit {value} value")
    @Min(value = 0 ,message = "purposeId exceed limit {value} value")
    private Integer purposeId;

    @Size(max = 100 ,message = "purposeCode exceed limit {max} chars")
    private String purposeCode;

    @Size(max = 500 ,message = "purposeName exceed limit {max} chars")
    private String purposeName;
}
