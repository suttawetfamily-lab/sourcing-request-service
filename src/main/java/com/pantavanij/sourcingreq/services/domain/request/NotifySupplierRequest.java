package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

@Data
public class NotifySupplierRequest {
    private String tenant;
    private String orgName;
    private String srName;
    private String srSubmitDate;
    private String opCat;
    private String opSubCat;
}
