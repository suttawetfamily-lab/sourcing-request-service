package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

import java.util.List;

@Data
public class PurchaseRequisitionRequest {
    private String requestNo;
    private List<Long> requestItemsId;
}
