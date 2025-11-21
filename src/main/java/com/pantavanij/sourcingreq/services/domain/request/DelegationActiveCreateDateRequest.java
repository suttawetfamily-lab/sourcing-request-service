package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

@Data
public class DelegationActiveCreateDateRequest {
    private String delegateeBy;
    private String delegatorBy;
}
