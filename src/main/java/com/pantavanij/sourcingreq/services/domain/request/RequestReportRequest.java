package com.pantavanij.sourcingreq.services.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pantavanij.sourcingreq.services.domain.dto.TenantRequestItemReportDto;
import com.pantavanij.sourcingreq.services.domain.dto.TenantRequestReportDto;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Data
public class RequestReportRequest {
    @NotNull(message = "RequestReport id is required")
    @PositiveOrZero(message = "RequestReport id must be positive or zero")
    private Integer id;
    @NotNull(message = "Privilege code is required!")
    private String privilegeCode;
    @NotBlank(message = "RequestReport code is required")
    private String code;
    @NotBlank(message = "RequestReport name is required")
    private String name;
    private boolean active;
    @JsonProperty("default")
    private boolean isDefault;
    @NotNull(message = "Sequence is required!")
    private Integer sequence;
    private List<TenantRequestReportDto> tenantRequestReportList;
}
