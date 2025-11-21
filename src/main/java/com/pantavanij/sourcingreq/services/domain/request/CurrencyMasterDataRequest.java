package com.pantavanij.sourcingreq.services.domain.request;

import lombok.*;

import javax.validation.constraints.*;

@Data
public class CurrencyMasterDataRequest {
    @NotNull(message = "Currency id is required")
    @PositiveOrZero(message = "Currency id must be positive or zero")
    private Integer recId;
    @NotBlank(message = "Currency code is required")
    private String code;
    @NotBlank(message = "Currency name is required")
    private String name;
}
