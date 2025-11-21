package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
public class ExistingPriceItemDtoV2 {
    //Request
    private Long requestId;
    private Integer requestTypeId;

    //RequestItem
    private Long requestItemId;
    private String requestStatusName;
    private String purposeDescription;
    private String item;
    private String itemDescription;
    private String budgetTypeName;
    private BigDecimal quantity;
    private String condition;
    private String currencyCode;
    private String currencyDesc;
    private String sourcingDocNo;
    private String sourcingDocId;
    private Integer sourcingTypeId;
    private String sourcingTypeName;
    private String deletionReason;
    private String cancellationReason;
    private String purchaser;
    private boolean usedToCopiedToPR;
    private boolean displayCopiedToPRIcon;
    private boolean isFreeItem;
    private OptionDto unitObj;
    private OptionDto purposeObj;
    private OptionDto categoryObj;
    private OptionDto subCategoryObj;
    private OptionDto currencyObj;
    private OptionDto vatTypeObj;
    private LocationDto deliveryLocation;
    private String location;
    private String contactName;
    private String contactPhone;
    private BigDecimal itemBudget;

    //ExistingPrice
    private Long existingPriceItemId;
    private String materialCode;
    private BigDecimal unitPrice;
    private String brand;
    private String partNo;
    private String comment;
    private SourcingStatusDto sourcingStatus;


    //Supplier
    private Integer supplierId;
    private String supplierName;
    private OptionDto supplierObj;

    private List<RequestItemAttachmentDto> requestItemAttachmentList;
    private List<ExistingPriceItemAttachmentDto> existingPriceItemAttachmentList;
    private List<ExistingPriceItemSupplierDto> existingPriceItemSupplierList;

    private String createdBy;
    private String createdByName;
    private Timestamp createdDate;
    private String updatedBy;
    private String updatedByName;
    private Timestamp updatedDate;
    private String locationName;

    private Timestamp bidValidityStartDate;
    private Timestamp bidValidityEndDate;

    private Integer itemSequence;
}
