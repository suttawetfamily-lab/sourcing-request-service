package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RequestTypeRequest {
    @NotNull(message = "requestId must not be null or empty" )
    @Max(value = 9223372036854775807L ,message = "requestId exceed limit {value} value")
    @Min(value = 0 ,message = "requestId exceed limit {value} value")
    private Long requestId;

    @NotNull(message = "typeId must not be null or empty" )
    @Max(value = 2147483647 ,message = "typeId exceed limit {value} value")
    @Min(value = 0 ,message = "typeId exceed limit {value} value")
    private Integer typeId;

    @Size(max = 100 ,message = "typeCode exceed limit {max} chars")
    private String typeCode;

    @Size(max = 500 ,message = "typeName exceed limit {max} chars")
    private String typeName;
}
