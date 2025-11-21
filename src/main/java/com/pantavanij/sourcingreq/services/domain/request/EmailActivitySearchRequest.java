package com.pantavanij.sourcingreq.services.domain.request;

import com.pantavanij.sourcingreq.services.constraint.*;
import lombok.*;

import java.util.*;

@Data
public class EmailActivitySearchRequest {
    @ConditionSearchConstraint
    private List<ConditionSearchRequest> conditionSearchList;
    private List<Integer> exceptReportLines;
    private String tenantId;
    private int page;
    private int pageSize;
    private String sortBy;
    private String sortOrder;
}
