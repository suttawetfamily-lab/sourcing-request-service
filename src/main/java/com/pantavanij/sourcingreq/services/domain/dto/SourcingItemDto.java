package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
public class SourcingItemDto {
    //Request
    private Long requestId;

    //RequestItem
    private Long requestItemId;
    private OptionDto vatTypeObj;
    private Integer vatTypeId;

    private String sourcingDocNo;
    private String sourcingDocId;
    private Integer sourcingTypeId;
    private String sourcingTypeName;
    private SourcingStatusDto sourcingStatus;
    private BigDecimal quantity;
    private String conditions;
    private String cancellationReason;
    private String deletionReason;

    //ExistingPrice
    private OptionDto currencyObj;
    private BigDecimal itemBudget;
    private BigDecimal unitPrice;
    private Integer existingPriceItemUnitId;
    private String existingPriceItemUnitCode;
    private String existingPriceItemName;
    private String existingPriceItemDescription;
    private Integer supplierId;
    private String supplierName;
    private String partNo;
    private String brand;
    private String comment;

    private List<ExistingPriceItemSupplierDto> existingPriceItemSupplierList;
    private List<RequestItemAttachmentDto> requestItemAttachmentList;

    private String createdBy;
    private String createdByName;
    private Timestamp createdDate;
    private String updatedBy;
    private String updatedByName;
    private Timestamp updatedDate;
    private String purchaserName;

    private boolean isFreeItem;

}
