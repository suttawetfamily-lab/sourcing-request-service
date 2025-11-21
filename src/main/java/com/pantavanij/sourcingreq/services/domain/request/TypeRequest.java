package com.pantavanij.sourcingreq.services.domain.request;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import javax.validation.constraints.*;

@Data
public class TypeRequest {
    @NotNull(message = "Id is required")
    @PositiveOrZero(message = "Id must be a positive number or zero!")
    private Integer id;
    @NotBlank(message = "Type code is required")
    private String code;
    @NotBlank(message = "Type name is required")
    private String name;
    @NotNull(message = "Sequence is required")
    @PositiveOrZero(message = "Sequence must be a positive number or zero!")
    private Integer sequence;
    @JsonProperty("default")
    private boolean isDefault;
    private boolean active;
}
