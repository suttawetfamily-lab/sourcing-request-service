package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class ExistingPriceItemResponseDto {
    private Long recId;
    private RequestDto request;
    private RequestItemV2Dto requestItemDto;
    private String materialCode;
    private String itemName;
    private String itemDescription;
    private String brand;
    private String partNo;
    private SupplierDto supplier;
    private String supplierName;
    private UnitDto unit;
    private BigDecimal unitPrice;
    private CurrencyDto currency;
    private String comment;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
}
