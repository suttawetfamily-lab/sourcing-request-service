package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RequestItemCurrencyRequest {
    @NotNull(message = "requestItemId must not be null or empty" )
    @Max(value = 9223372036854775807L ,message = "requestItemId exceed limit {value} value")
    @Min(value = 0 ,message = "requestItemId exceed limit {value} value")
    private Long requestItemId;

    @NotNull(message = "currencyId must not be null or empty" )
    @Max(value = 2147483647 ,message = "currencyId exceed limit {value} value")
    @Min(value = 0 ,message = "currencyId exceed limit {value} value")
    private Integer currencyId;

    @Size(max = 100 ,message = "currencyCode exceed limit {max} chars")
    private String currencyCode;

    @Size(max = 500 ,message = "currencyName exceed limit {max} chars")
    private String currencyName;
}
