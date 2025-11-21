package com.pantavanij.sourcingreq.services.domain.request;

import com.pantavanij.sourcingreq.services.domain.dto.DelegationStatusDto;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class DelegationSearchRequest {
    private List<ConditionSearchRequest> conditionSearchList;
    private List<DelegationStatusDto> delegationStatusList;
    private Long delegationStatusId;
    private Timestamp fromDate;
    private Timestamp toDate;
    private Timestamp period;
    private int page;
    private int pageSize;
    private String sortBy;
    private String sortOrder;
}
