package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

@Data
public class DelegationActiveRequest {
    private String delegateeBy;
    private String delegatorBy;
}

