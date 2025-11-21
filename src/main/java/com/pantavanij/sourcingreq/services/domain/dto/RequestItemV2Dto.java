package com.pantavanij.sourcingreq.services.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestItemV2Dto {
    private Long recId;
    private String sourcingDocNo;
    private String sourcingDocId;
    private Integer sourcingTypeId;
    private SourcingStatusDto sourcingStatus;
    private OptionDto unitObj;
    private String purposeDescription;
    private String itemName;
    private String itemDescription;
    private BigDecimal itemBudget;
    private String brand;
    private String partNo;
    private String conditions;
    private BigDecimal quantity;
    private String deletionReason;
    private String cancellationReason;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
    private ExistingPriceItemResponseDto existingPriceItemDto;
    private List<RequestItemLocationDto> requestItemLocationList;
    private List<RequestItemAttachmentDto> requestItemAttachmentList;
    private OptionDto purposeObj;
    private OptionDto categoryObj;
//    private OptionDto subCategoryObj;
    private TenantSubCategoryDto subCategoryObj;
    private OptionDto currencyObj;
    private LocationDto deliveryLocation;
    private String location;
    private String contactName;
    private String contactPhone;
    private String bidValidityStartDate;
    private String bidValidityEndDate;
    private String purchaser;
    private Integer itemSequence;
    private String displayPurchaser;
}
