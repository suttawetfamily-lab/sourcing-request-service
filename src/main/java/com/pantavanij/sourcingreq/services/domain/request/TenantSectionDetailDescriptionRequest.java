package com.pantavanij.sourcingreq.services.domain.request;

import lombok.*;

import javax.validation.constraints.*;

@Data
public class TenantSectionDetailDescriptionRequest {
    @NotNull(message = "Tenant section id is required!")
    @PositiveOrZero(message = "Tenant section id must be positive or zero!")
    private Integer tenantSectionDetailId;
    @NotBlank(message = "Description is required!")
    private String description;
    private String descriptionLabelAction;
    private String descriptionAction;
    @NotNull(message = "Tenant section detail description disable is required!")
    @PositiveOrZero(message = "Tenant section detail description disable must be positive or zero!")
    private Integer disable;
}
