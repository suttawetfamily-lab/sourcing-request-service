package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ExistingPriceItemCategoryRequest {
    @NotNull(message = "existingPriceItemId must not be null or empty" )
    @Max(value = 9223372036854775807L ,message = "existingPriceItemId exceed limit {value} value")
    @Min(value = 0 ,message = "existingPriceItemId exceed limit {value} value")
    private Long existingPriceItemId;

    @NotNull(message = "categoryId must not be null or empty" )
    @Max(value = 2147483647 ,message = "categoryId exceed limit {value} value")
    @Min(value = 0 ,message = "categoryId exceed limit {value} value")
    private Integer categoryId;

    @Size(max = 100 ,message = "categoryCode exceed limit {max} chars")
    private String categoryCode;

    @Size(max = 500 ,message = "categoryName exceed limit {max} chars")
    private String categoryName;
}
