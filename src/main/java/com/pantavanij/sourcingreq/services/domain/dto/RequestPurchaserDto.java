package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class RequestPurchaserDto {

    private RequestDto requestDto;
    private PurchaserDto purchaserDto;

    private Long recId;
    private Long requestId;
    private Integer tenantId;
    private Long workflowInstanceId;
    private String purchaserName;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
}
