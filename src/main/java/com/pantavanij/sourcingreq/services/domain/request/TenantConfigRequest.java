package com.pantavanij.sourcingreq.services.domain.request;

import lombok.*;

import javax.validation.constraints.*;

@Data
public class TenantConfigRequest {
    @NotNull(message = "RecId is required!")
    private Integer recId;
    @NotBlank(message = "Topic is required!")
    private String topic;
    @NotBlank(message = "Section is required!")
    private String section;
    @NotBlank(message = "Name is required!")
    private String name;
    @NotBlank(message = "Value is required!")
    private String value;
    @PositiveOrZero(message = "Sequence must be a positive number or zero!")
    @NotNull(message = "Sequence is required!")
    private Integer sequence;
    private String description;
}
