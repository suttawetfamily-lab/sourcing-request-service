package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

import java.util.List;

@Data
public class DelegateeByDelegatorRequest {
    private String delegatorUserName;
    private List<Long> delegationStatuses;
}
