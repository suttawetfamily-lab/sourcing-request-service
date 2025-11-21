package com.pantavanij.sourcingreq.services.domain.request;

import com.pantavanij.sourcingreq.services.constraint.ConditionSearchConstraint;
import com.pantavanij.sourcingreq.services.domain.dto.ApprovalStatusDto;
import com.pantavanij.sourcingreq.services.domain.dto.DeptApprovalStatusDto;
import com.pantavanij.sourcingreq.services.domain.dto.ExcSourcingStatusDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ExcSourcingApproverSearchRequest {
//    private Long recId;
    @ConditionSearchConstraint
    private List<ConditionSearchRequest> conditionSearchList;
    private Tenant tenant;
    private List<ExcSourcingStatusDto> excSourcingStatusList;
    private List<DeptApprovalStatusDto> approveStatusList;
    private Date requestFromDate;
    private Date requestToDate;
    private int page;
    private int pageSize;
    private String sortBy;
    private String sortOrder;
}
