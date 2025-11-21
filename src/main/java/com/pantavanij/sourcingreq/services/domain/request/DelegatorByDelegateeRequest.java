package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

import java.util.List;

@Data
public class DelegatorByDelegateeRequest {
    private String delegateeUserName;
    private List<Long> delegationStatuses;
}
