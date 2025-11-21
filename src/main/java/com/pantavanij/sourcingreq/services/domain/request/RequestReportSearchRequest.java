package com.pantavanij.sourcingreq.services.domain.request;

import com.pantavanij.sourcingreq.services.constraint.ConditionSearchConstraint;
import lombok.Data;

import java.util.List;

@Data
public class RequestReportSearchRequest {
    @ConditionSearchConstraint
    private List<ConditionSearchRequest> conditionSearchList;
    private Long requestId;
    private String tenantId;
    private int page;
    private int pageSize;
    private String sortBy;
    private String sortOrder;
}
