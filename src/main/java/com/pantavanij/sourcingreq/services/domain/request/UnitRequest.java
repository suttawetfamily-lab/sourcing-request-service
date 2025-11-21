package com.pantavanij.sourcingreq.services.domain.request;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import javax.validation.constraints.*;

@Data
public class UnitRequest {
    @NotNull(message = "Unit id is required")
    @PositiveOrZero(message = "Unit id must be positive or zero")
    private Integer id;
    private boolean active;
    @NotBlank(message = "Unit code is required")
    private String code;
    @JsonProperty("default")
    private boolean isDefault;
    @NotBlank(message = "Unit name is required")
    private String name;
    @NotNull(message = "Sequence is required!")
    private Integer sequence;
}
