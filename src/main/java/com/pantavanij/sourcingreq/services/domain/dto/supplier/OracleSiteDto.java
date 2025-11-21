package com.pantavanij.sourcingreq.services.domain.dto.supplier;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OracleSiteDto {

    @JsonProperty("SupplierSite")
    private String supplierSite;

    @JsonProperty("ProcurementBU")
    private String procurementBU;

    @JsonProperty("SupplierAddressName")
    private String supplierAddressName;

    @JsonProperty("SitePurposePurchasingFlag")
    private Boolean sitePurposePurchasingFlag;

    @JsonProperty("SitePurposePayFlag")
    private Boolean sitePurposePayFlag;

    @JsonProperty("PayOnReceiptFlag")
    private Boolean payOnReceiptFlag;

    @JsonProperty("MatchApprovalLevelCode")
    private String matchApprovalLevelCode;

    @JsonProperty("PaymentPriority")
    private String paymentPriority;
}