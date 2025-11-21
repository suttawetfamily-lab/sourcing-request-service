package com.pantavanij.sourcingreq.services.domain.request;

import lombok.*;

import javax.validation.constraints.*;

@Data
public class TenantSectionDetailValidatorRequest {
    @NotNull(message = "Tenant section id is required!")
    @PositiveOrZero(message = "Tenant section id must be positive or zero!")
    private Integer tenantSectionDetailId;
    @NotNull(message = "Validator id is required!")
    @PositiveOrZero(message = "Validator id must be positive or zero!")
    private Integer validatorId;
    @NotNull(message = "Tenant section detail dependency sequence is required!")
    @PositiveOrZero(message = "Tenant section detail dependency sequence must be positive or zero!")
    private Integer sequence;
    private String name;
    private String value;
}
