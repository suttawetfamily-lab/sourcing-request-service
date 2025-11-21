package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DelegationCancelRequest {
    private Long recId;
    private String cancelReason;
}
