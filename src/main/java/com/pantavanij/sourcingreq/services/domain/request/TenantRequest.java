package com.pantavanij.sourcingreq.services.domain.request;

import lombok.*;

import javax.validation.constraints.*;

@Data
public class TenantRequest {
    @NotNull(message = "Tenant id must not be null or empty")
    @PositiveOrZero(message = "Tenant id must be positive or zero")
    private Integer recId;
    @NotBlank(message = "Tenant code must not be null or empty")
    private String code;
    @NotBlank(message = "Tenant name must not be null or empty")
    private String name;
    @NotBlank(message = "Tenant description must not be null or empty")
    private String description;
}
