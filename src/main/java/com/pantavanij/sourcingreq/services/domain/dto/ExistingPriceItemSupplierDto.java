package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class ExistingPriceItemSupplierDto {
    private BigDecimal awardedNetAmount;
    private BigDecimal unitPrice;
    private boolean isFreeItem;
    private OptionDto supplierObj;
    private SupplierDto supplier;
    private String awardedType;
    private String awardedValue;
    private String supplierShortName;
    private String supplierFullName;
    private String taxId;

    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
}
