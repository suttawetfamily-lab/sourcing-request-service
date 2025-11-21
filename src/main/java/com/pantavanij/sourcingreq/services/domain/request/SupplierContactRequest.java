package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

@Data
public class SupplierContactRequest {
    private Integer pageNo;
    private Integer pageSize;
    private Integer catLevel1Id;
    private Integer catLevel2Id;
    private Integer catLevel3Id;
    private String ruleName;
}
