package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class RequestItemGridFieldSequenceRequest {
    @NotNull(message = "RequestItemGridField Id (RecId) is required!")
    private Integer recId;
    @NotNull(message = "PrivilegeCode is required!")
    private String privilegeCode;
    @Positive(message = "Sequence must be a positive number!")
    @NotNull(message = "Sequence is required!")
    private Integer sequence;
    private Integer organizationId;
}
