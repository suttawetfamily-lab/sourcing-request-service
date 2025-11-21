package com.pantavanij.sourcingreq.services.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pantavanij.sourcingreq.services.constraint.ConditionSearchConstraint;
import com.pantavanij.sourcingreq.services.domain.dto.TenantDto;
import com.pantavanij.sourcingreq.services.domain.dto.eform.EFormStatusDto;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class EFormSearchRequest {
    private Long recId;
    @ConditionSearchConstraint
    private List<ConditionSearchRequest> conditionSearchList;
    private TenantDto tenant;
    private List<EFormStatusDto> statusList;
    private Date fromDate;
    private Date toDate;
    private int page;
    private int pageSize;
    private String sortBy;
    private String sortOrder;
    private String type;
    private String service;
    @JsonProperty("isCallEForm")
    private boolean isCallEForm;
    private Boolean checkPermission;
}
