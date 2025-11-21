package com.pantavanij.sourcingreq.services.domain.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EPAuthUserSearchRequest {
    private List<ConditionSearchRequest> conditionSearchList;
    private String tenantId;
    private int page;
    private int pageSize;
    private String sortBy;
    private String sortOrder;
}
