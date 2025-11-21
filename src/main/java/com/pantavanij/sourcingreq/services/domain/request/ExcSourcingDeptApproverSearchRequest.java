package com.pantavanij.sourcingreq.services.domain.request;

import com.pantavanij.sourcingreq.services.constraint.ConditionSearchConstraint;
import com.pantavanij.sourcingreq.services.domain.dto.DeptApprovalStatusDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ExcSourcingDeptApproverSearchRequest {
//    private Long recId;
    @ConditionSearchConstraint
    private List<ConditionSearchRequest> conditionSearchList;
    private Tenant tenant;
    private List<DeptApprovalStatusDto> approveStatusList;
    private Date fromDate;
    private Date toDate;
    private int page;
    private int pageSize;
    private String sortBy;
    private String sortOrder;
}
