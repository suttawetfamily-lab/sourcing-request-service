package com.pantavanij.sourcingreq.services.domain.request;

import com.pantavanij.sourcingreq.services.constraint.*;
import lombok.*;

import java.util.*;

@Data
public class TenantSectionSearchRequest {
    @ConditionSearchConstraint
    private List<ConditionSearchRequest> conditionSearchList;
    private String tenantId;
    private Integer organizationId;
    private int page;
    private int pageSize;
    private String sortBy;
    private String sortOrder;
}
