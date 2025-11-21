package com.pantavanij.sourcingreq.services.domain.request;

import com.pantavanij.sourcingreq.services.constraint.ConditionSearchConstraint;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import lombok.Data;

import java.util.List;

@Data
public class CategorySearchRequest {
    @ConditionSearchConstraint
    private List<ConditionSearchRequest> conditionSearchList;
    private Tenant tenant;
    private int page;
    private int pageSize;
    private String sortBy;
    private String sortOrder;
}
