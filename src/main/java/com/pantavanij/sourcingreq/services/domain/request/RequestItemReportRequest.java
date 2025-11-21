package com.pantavanij.sourcingreq.services.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pantavanij.sourcingreq.services.domain.dto.TenantRequestItemReportDto;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Data
public class RequestItemReportRequest {
    @NotNull(message = "RequestItemReport id is required")
    @PositiveOrZero(message = "RequestItemReport id must be positive or zero")
    private Integer id;
    @NotNull(message = "MenuPrivilege id is required!")
    private Integer menuPrivilegeId;
    @NotNull(message = "Privilege code is required!")
    private String privilegeCode;
    @NotNull(message = "ExportFileName is required!")
    private String exportFileName;
    @NotBlank(message = "RequestItemReport code is required")
    private String code;
    @NotBlank(message = "RequestItemReport name is required")
    private String name;
    private boolean active;
    @JsonProperty("default")
    private boolean isDefault;
    @NotNull(message = "Sequence is required!")
    private Integer sequence;
    private List<TenantRequestItemReportDto> tenantRequestItemReportList;
}
