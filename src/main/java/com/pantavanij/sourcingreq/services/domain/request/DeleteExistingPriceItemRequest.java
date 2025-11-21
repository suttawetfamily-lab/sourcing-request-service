package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class DeleteExistingPriceItemRequest {
    @NotNull(message = "existingPriceItemId must not be null or empty" )
    @Max(value = 9223372036854775807L ,message = "existingPriceItemId exceed limit {value} value")
    @Min(value = 0 ,message = "existingPriceItemId exceed limit {value} value")
    private Long existingPriceItemId;

    @Size(max = 2000 ,message = "cancellationReason exceed limit {max} chars")
    private String deletionReason;
}
