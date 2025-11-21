package com.pantavanij.sourcingreq.services.domain.request;

import lombok.*;
import javax.validation.constraints.*;

@Data
public class UnitMasterDataRequest {
    @NotNull(message = "Unit id is required")
    @PositiveOrZero(message = "Unit id must be positive or zero")
    private Integer recId;
    @NotBlank(message = "Unit code is required")
    private String code;
    @NotBlank(message = "Unit name is required")
    private String name;
}