package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class RequestItemSourcingStatusRequest {
    @NotNull(message = "sourcingStatusId must not be null or empty")
    private Integer sourcingStatusId;
    @NotBlank(message = "sourcingDocNo must not be null or empty")
    private String sourcingDocNo;
}
