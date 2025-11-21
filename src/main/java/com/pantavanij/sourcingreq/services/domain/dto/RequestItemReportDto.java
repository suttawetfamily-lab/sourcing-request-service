package com.pantavanij.sourcingreq.services.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantRequestItemReport;
import lombok.Data;
import java.sql.Timestamp;
import java.util.List;

@Data
public class RequestItemReportDto {
    private Integer recId;
    private RequestTenantDto tenant;
    @JsonProperty("menuPrivilegeObj")
    private MenuPrivilegeObjDto menuPrivilege;
    private String code;
    private String name;
    private String privilegeCode;
    private String exportFileName;
    private Integer sequence;
    private boolean isDefault;
    private boolean active;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
    private List<TenantRequestItemReportDto> tenantRequestItemReportList;
    private Integer templateId;
}
