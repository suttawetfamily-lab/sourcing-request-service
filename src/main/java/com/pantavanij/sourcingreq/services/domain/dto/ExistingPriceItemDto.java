package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
public class ExistingPriceItemDto {
    //Request
    private Long requestId;
    private Integer requestTypeId;

    //RequestItem
    private BigDecimal quantity;
    private Long requestItemId;
    private OptionDto categoryObj;
    private OptionDto subCategoryObj;
    private OptionDto vatTypeObj;
    private Integer vatTypeId;
    private String budgetTypeName;
    private String condition;
    private String currencyCode;
    private String currencyDesc;
    private String contactName;
    private String cancellationReason;
    private String deletionReason;
    private String deliveryLocation;
    private String itemName;
    private String itemDescription;
    private String phone;
    private String purchaserName;
    private String purposeDescription;
    private String requestStatusName;
    private String sourcingDocNo;
    private String sourcingDocId;
    private Integer sourcingTypeId;
    private String sourcingTypeName;
    private String unitCode;
    private boolean usedToCopiedToPR;
    private boolean displayCopiedToPRIcon;
    private boolean isFreeItem;


    //ExistingPrice
    private BigDecimal unitPrice;
    private Integer existingPriceItemUnitId;
    private Long existingPriceItemId;
    private String brand;
    private String comment;
    private String existingPriceItemUnitCode;
    private String existingPriceItemName;
    private String existingPriceItemDescription;
    private String materialCode;
    private String partNo;
    private SourcingStatusDto sourcingStatus;


    //Supplier
    private Integer supplierId;
    private Integer itemSequence;

    private List<RequestItemAttachmentDto> requestItemAttachmentList;
    private List<ExistingPriceItemAttachmentDto> existingPriceItemAttachmentList;
    private List<ExistingPriceItemSupplierDto> existingPriceItemSupplierList;

    private String createdBy;
    private String createdByName;
    private String locationName;
    private String updatedBy;
    private String updatedByName;
    private String supplierName;
    private Timestamp bidValidityStartDate;
    private Timestamp bidValidityEndDate;
    private Timestamp createdDate;
    private Timestamp updatedDate;
}
