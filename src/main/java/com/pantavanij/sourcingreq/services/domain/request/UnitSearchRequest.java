package com.pantavanij.sourcingreq.services.domain.request;

import lombok.*;
import java.util.*;

@Data
public class UnitSearchRequest {
    private List<ConditionSearchRequest> conditionSearchList;
    private String tenantId;
    private int page;
    private int pageSize;
    private String sortBy;
    private String sortOrder;
}
