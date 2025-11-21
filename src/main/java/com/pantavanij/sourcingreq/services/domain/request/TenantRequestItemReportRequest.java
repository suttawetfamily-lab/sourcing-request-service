package com.pantavanij.sourcingreq.services.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
public class TenantRequestItemReportRequest {
    @NotNull(message = "TenantRequestItemReport id is required")
    @PositiveOrZero(message = "TenantRequestItemReport id must be positive or zero")
    private Integer id;
    @NotNull(message = "RequestItemReport id is required")
    @PositiveOrZero(message = "RequestItemReport id must be positive or zero")
    private Integer requestItemReportId;
    @NotNull(message = "TenantSectionDetail id is required")
    @PositiveOrZero(message = "TenantSectionDetail id must be positive or zero")
    private Integer tenantSectionDetailId;
    @JsonProperty("default")
    private boolean isDefault;
    private boolean active;
    @NotNull(message = "Sequence is required!")
    private Integer sequence;
}
