package com.pantavanij.sourcingreq.services.domain.request;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import javax.validation.constraints.*;

@Data
public class ProjectRequest {
    @NotNull(message = "Project recId is required")
    private Integer recId;
    @NotEmpty(message = "Project code is required")
    private String code;
    @NotEmpty(message = "Project name is required")
    private String name;
    @Positive(message = "Sequence must be a positive number!")
    @NotNull(message = "Sequence is required!")
    private Integer sequence;
    @JsonProperty("default")
    private boolean isDefault;
    private boolean active;
}
