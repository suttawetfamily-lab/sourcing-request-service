package com.pantavanij.sourcingreq.services.domain.request;

import lombok.*;

import javax.validation.constraints.*;

@Data
public class TenantSectionDetailDataSourceRequest {
    @NotNull(message = "Tenant section detail id is required!")
    @PositiveOrZero(message = "Tenant section detail id must be positive or zero!")
    private Integer tenantSectionDetailId;
    @NotNull(message = "Datasource id is required!")
    @PositiveOrZero(message = "Datasource id must be positive or zero!")
    private Integer dataSourceId;
}
