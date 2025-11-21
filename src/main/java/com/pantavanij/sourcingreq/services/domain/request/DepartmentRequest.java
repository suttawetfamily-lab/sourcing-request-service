package com.pantavanij.sourcingreq.services.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.*;

@Data
public class DepartmentRequest {
    @NotNull(message = "Department Id (RecId) is required!")
    private Integer recId;
    @NotBlank(message = "Code is required!")
    private String code;
    @NotBlank(message = "Name is required!")
    private String name;
    @PositiveOrZero(message = "Sequence must be a positive number or zero!")
    @NotNull(message = "Sequence is required!")
    private Integer sequence;
    @JsonProperty("default")
    @NotNull(message = "Default is required!")
    private boolean isDefault;
    @NotNull(message = "Active is required!")
    private boolean active;
}
