package com.pantavanij.sourcingreq.services.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TenantSourcingStatusRequest {
    @NotNull(message = "SourcingStatusId is required!")
    private Integer sourcingStatusId;
    @NotNull(message = "PurchaserOwner is required!")
    private String purchaserOwner;
    @NotNull(message = "PurchaserNotOwner is required!")
    private String purchaserNotOwner;
    @NotNull(message = "Requester is required!")
    private String requester;
    @NotNull(message = "Reviewer is required!")
    private String reviewer;
    @NotNull(message = "Approver is required!")
    private String approver;
    @NotNull(message = "Remark is required!")
    private String remark;
    @NotNull(message = "Sequence is required!")
    private Integer sequence;
    private boolean active;
    @JsonProperty("default")
    private boolean isDefault;
}
