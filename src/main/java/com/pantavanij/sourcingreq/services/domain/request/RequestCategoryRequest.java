package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class RequestCategoryRequest {
    @NotNull(message = "requestId must not be null or empty" )
    @Max(value = 9223372036854775807L ,message = "requestId exceed limit {value} value")
    @Min(value = 0 ,message = "requestId exceed limit {value} value")
    private Long requestId;

    @NotNull(message = "categoryId must not be null or empty" )
    @Max(value = 2147483647 ,message = "categoryId exceed limit {value} value")
    @Min(value = 0 ,message = "categoryId exceed limit {value} value")
    private Integer categoryId;

    @Size(max = 100 ,message = "categoryCode exceed limit {max} chars")
    private String categoryCode;

    @Size(max = 500 ,message = "categoryName exceed limit {max} chars")
    private String categoryName;
}
