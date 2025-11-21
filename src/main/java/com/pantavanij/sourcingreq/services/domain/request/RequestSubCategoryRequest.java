package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RequestSubCategoryRequest {
    @NotNull(message = "requestId must not be null or empty" )
    @Max(value = 9223372036854775807L ,message = "requestId exceed limit {value} value")
    @Min(value = 0 ,message = "requestId exceed limit {value} value")
    private Long requestId;

    @NotNull(message = "categoryId must not be null or empty" )
    @Max(value = 2147483647 ,message = "categoryId exceed limit {value} value")
    @Min(value = 0 ,message = "subCategoryId exceed limit {value} value")
    private Integer subCategoryId;

    @Size(max = 100 ,message = "subCategoryCode exceed limit {max} chars")
    private String subCategoryCode;

    @Size(max = 500 ,message = "subCategoryName exceed limit {max} chars")
    private String subCategoryName;
}
