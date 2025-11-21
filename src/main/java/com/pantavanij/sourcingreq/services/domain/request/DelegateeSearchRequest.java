package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

import java.util.List;

@Data
public class DelegateeSearchRequest {
    private List<ConditionSearchRequest> conditionSearchList;
    private List<String> exceptDelegatees;
    private String searchTerm;
    private String tenantId;
    private int page;
    private int pageSize;
    private String sortBy;
    private String sortOrder;
}
