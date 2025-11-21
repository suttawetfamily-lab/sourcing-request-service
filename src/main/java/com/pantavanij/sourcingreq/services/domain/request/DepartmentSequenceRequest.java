package com.pantavanij.sourcingreq.services.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class DepartmentSequenceRequest {
    @NotNull(message = "Department Id (RecId) is required!")
    private Integer recId;
    @Positive(message = "Sequence must be a positive number!")
    @NotNull(message = "Sequence is required!")
    private Integer sequence;
}
